package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Keep;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.GL;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraInfo;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.camera.Size;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.video.Mp4Movie;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;

@TargetApi(18)
public class InstantCameraView
  extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final String FRAGMENT_SCREEN_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
  private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float scaleX;\nuniform float scaleY;\nuniform float alpha;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec2 coord = vec2((vTextureCoord.x - 0.5) * scaleX, (vTextureCoord.y - 0.5) * scaleY);\n   float coef = ceil(clamp(0.2601 - dot(coord, coord), 0.0, 1.0));\n   vec3 color = texture2D(sTexture, vTextureCoord).rgb * coef + (1.0 - step(0.001, coef));\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n";
  private static final int MSG_AUDIOFRAME_AVAILABLE = 3;
  private static final int MSG_START_RECORDING = 0;
  private static final int MSG_STOP_RECORDING = 1;
  private static final int MSG_VIDEOFRAME_AVAILABLE = 2;
  private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
  private AnimatorSet animatorSet;
  private Size aspectRatio;
  private ChatActivity baseFragment;
  private FrameLayout cameraContainer;
  private File cameraFile;
  private volatile boolean cameraReady;
  private CameraSession cameraSession;
  private int[] cameraTexture = new int[1];
  private float cameraTextureAlpha = 1.0F;
  private CameraGLThread cameraThread;
  private boolean cancelled;
  private int currentAccount = UserConfig.selectedAccount;
  private boolean deviceHasGoodCamera;
  private long duration;
  private TLRPC.InputEncryptedFile encryptedFile;
  private TLRPC.InputFile file;
  private boolean isFrontface = true;
  private boolean isSecretChat;
  private byte[] iv;
  private byte[] key;
  private float[] mMVPMatrix;
  private float[] mSTMatrix;
  private float[] moldSTMatrix;
  private AnimatorSet muteAnimation;
  private ImageView muteImageView;
  private int[] oldCameraTexture = new int[1];
  private Paint paint;
  private Size pictureSize;
  private int[] position = new int[2];
  private Size previewSize;
  private float progress;
  private Timer progressTimer;
  private long recordStartTime;
  private long recordedTime;
  private boolean recording;
  private RectF rect;
  private boolean requestingPermissions;
  private float scaleX;
  private float scaleY;
  private CameraInfo selectedCamera;
  private long size;
  private ImageView switchCameraButton;
  private FloatBuffer textureBuffer;
  private TextureView textureView;
  private Runnable timerRunnable = new Runnable()
  {
    public void run()
    {
      if (!InstantCameraView.this.recording) {}
      for (;;)
      {
        return;
        NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordProgressChanged, new Object[] { Long.valueOf(InstantCameraView.access$102(InstantCameraView.this, System.currentTimeMillis() - InstantCameraView.this.recordStartTime)), Double.valueOf(0.0D) });
        AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable, 50L);
      }
    }
  };
  private FloatBuffer vertexBuffer;
  private VideoEditedInfo videoEditedInfo;
  private VideoPlayer videoPlayer;
  
  public InstantCameraView(Context paramContext, final ChatActivity paramChatActivity)
  {
    super(paramContext);
    final Object localObject;
    boolean bool;
    if (SharedConfig.roundCamera16to9)
    {
      localObject = new Size(16, 9);
      this.aspectRatio = ((Size)localObject);
      this.mMVPMatrix = new float[16];
      this.mSTMatrix = new float[16];
      this.moldSTMatrix = new float[16];
      setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          float f1 = 1.0F;
          boolean bool;
          float f2;
          if ((paramAnonymousMotionEvent.getAction() == 0) && (InstantCameraView.this.baseFragment != null))
          {
            if (InstantCameraView.this.videoPlayer == null) {
              break label304;
            }
            if (InstantCameraView.this.videoPlayer.isMuted()) {
              break label278;
            }
            bool = true;
            InstantCameraView.this.videoPlayer.setMute(bool);
            if (InstantCameraView.this.muteAnimation != null) {
              InstantCameraView.this.muteAnimation.cancel();
            }
            InstantCameraView.access$702(InstantCameraView.this, new AnimatorSet());
            paramAnonymousView = InstantCameraView.this.muteAnimation;
            paramAnonymousMotionEvent = InstantCameraView.this.muteImageView;
            if (!bool) {
              break label284;
            }
            f2 = 1.0F;
            label116:
            paramAnonymousMotionEvent = ObjectAnimator.ofFloat(paramAnonymousMotionEvent, "alpha", new float[] { f2 });
            Object localObject = InstantCameraView.this.muteImageView;
            if (!bool) {
              break label290;
            }
            f2 = 1.0F;
            label148:
            localObject = ObjectAnimator.ofFloat(localObject, "scaleX", new float[] { f2 });
            ImageView localImageView = InstantCameraView.this.muteImageView;
            if (!bool) {
              break label297;
            }
            f2 = f1;
            label182:
            paramAnonymousView.playTogether(new Animator[] { paramAnonymousMotionEvent, localObject, ObjectAnimator.ofFloat(localImageView, "scaleY", new float[] { f2 }) });
            InstantCameraView.this.muteAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnonymous2Animator)
              {
                if (paramAnonymous2Animator.equals(InstantCameraView.this.muteAnimation)) {
                  InstantCameraView.access$702(InstantCameraView.this, null);
                }
              }
            });
            InstantCameraView.this.muteAnimation.setDuration(180L);
            InstantCameraView.this.muteAnimation.setInterpolator(new DecelerateInterpolator());
            InstantCameraView.this.muteAnimation.start();
          }
          for (;;)
          {
            return true;
            label278:
            bool = false;
            break;
            label284:
            f2 = 0.0F;
            break label116;
            label290:
            f2 = 0.5F;
            break label148;
            label297:
            f2 = 0.5F;
            break label182;
            label304:
            InstantCameraView.this.baseFragment.checkRecordLocked();
          }
        }
      });
      setWillNotDraw(false);
      setBackgroundColor(-NUM);
      this.baseFragment = paramChatActivity;
      if (this.baseFragment.getCurrentEncryptedChat() == null) {
        break label453;
      }
      bool = true;
      label143:
      this.isSecretChat = bool;
      this.paint = new Paint(1)
      {
        public void setAlpha(int paramAnonymousInt)
        {
          super.setAlpha(paramAnonymousInt);
          InstantCameraView.this.invalidate();
        }
      };
      this.paint.setStyle(Paint.Style.STROKE);
      this.paint.setStrokeCap(Paint.Cap.ROUND);
      this.paint.setStrokeWidth(AndroidUtilities.dp(3.0F));
      this.paint.setColor(-1);
      this.rect = new RectF();
      if (Build.VERSION.SDK_INT < 21) {
        break label459;
      }
      this.cameraContainer = new FrameLayout(paramContext)
      {
        public void setAlpha(float paramAnonymousFloat)
        {
          super.setAlpha(paramAnonymousFloat);
          InstantCameraView.this.invalidate();
        }
        
        public void setScaleX(float paramAnonymousFloat)
        {
          super.setScaleX(paramAnonymousFloat);
          InstantCameraView.this.invalidate();
        }
      };
      this.cameraContainer.setOutlineProvider(new ViewOutlineProvider()
      {
        @TargetApi(21)
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          paramAnonymousOutline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
        }
      });
      this.cameraContainer.setClipToOutline(true);
      this.cameraContainer.setWillNotDraw(false);
    }
    for (;;)
    {
      addView(this.cameraContainer, new FrameLayout.LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize, 17));
      this.switchCameraButton = new ImageView(paramContext);
      this.switchCameraButton.setScaleType(ImageView.ScaleType.CENTER);
      addView(this.switchCameraButton, LayoutHelper.createFrame(48, 48.0F, 83, 20.0F, 0.0F, 0.0F, 14.0F));
      this.switchCameraButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if ((!InstantCameraView.this.cameraReady) || (InstantCameraView.this.cameraSession == null) || (!InstantCameraView.this.cameraSession.isInitied()) || (InstantCameraView.this.cameraThread == null)) {}
          for (;;)
          {
            return;
            InstantCameraView.this.switchCamera();
            paramAnonymousView = ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, "scaleX", new float[] { 0.0F }).setDuration(100L);
            paramAnonymousView.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnonymous2Animator)
              {
                paramAnonymous2Animator = InstantCameraView.this.switchCameraButton;
                if (InstantCameraView.this.isFrontface) {}
                for (int i = NUM;; i = NUM)
                {
                  paramAnonymous2Animator.setImageResource(i);
                  ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, "scaleX", new float[] { 1.0F }).setDuration(100L).start();
                  return;
                }
              }
            });
            paramAnonymousView.start();
          }
        }
      });
      this.muteImageView = new ImageView(paramContext);
      this.muteImageView.setScaleType(ImageView.ScaleType.CENTER);
      this.muteImageView.setImageResource(NUM);
      this.muteImageView.setAlpha(0.0F);
      addView(this.muteImageView, LayoutHelper.createFrame(48, 48, 17));
      ((FrameLayout.LayoutParams)this.muteImageView.getLayoutParams()).topMargin = (AndroidUtilities.roundMessageSize / 2 - AndroidUtilities.dp(24.0F));
      setVisibility(4);
      return;
      localObject = new Size(4, 3);
      break;
      label453:
      bool = false;
      break label143;
      label459:
      paramChatActivity = new Path();
      localObject = new Paint(1);
      ((Paint)localObject).setColor(-16777216);
      ((Paint)localObject).setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      this.cameraContainer = new FrameLayout(paramContext)
      {
        protected void dispatchDraw(Canvas paramAnonymousCanvas)
        {
          try
          {
            super.dispatchDraw(paramAnonymousCanvas);
            paramAnonymousCanvas.drawPath(paramChatActivity, localObject);
            return;
          }
          catch (Exception paramAnonymousCanvas)
          {
            for (;;) {}
          }
        }
        
        protected void onSizeChanged(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          super.onSizeChanged(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
          paramChatActivity.reset();
          paramChatActivity.addCircle(paramAnonymousInt1 / 2, paramAnonymousInt2 / 2, paramAnonymousInt1 / 2, Path.Direction.CW);
          paramChatActivity.toggleInverseFillType();
        }
        
        public void setScaleX(float paramAnonymousFloat)
        {
          super.setScaleX(paramAnonymousFloat);
          InstantCameraView.this.invalidate();
        }
      };
      this.cameraContainer.setWillNotDraw(false);
      this.cameraContainer.setLayerType(2, null);
    }
  }
  
  private void createCamera(final SurfaceTexture paramSurfaceTexture)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (InstantCameraView.this.cameraThread == null) {}
        for (;;)
        {
          return;
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("create camera session");
          }
          paramSurfaceTexture.setDefaultBufferSize(InstantCameraView.this.previewSize.getWidth(), InstantCameraView.this.previewSize.getHeight());
          InstantCameraView.access$1002(InstantCameraView.this, new CameraSession(InstantCameraView.this.selectedCamera, InstantCameraView.this.previewSize, InstantCameraView.this.pictureSize, 256));
          InstantCameraView.this.cameraThread.setCurrentSession(InstantCameraView.this.cameraSession);
          CameraController.getInstance().openRound(InstantCameraView.this.cameraSession, paramSurfaceTexture, new Runnable()new Runnable
          {
            public void run()
            {
              if (InstantCameraView.this.cameraSession != null)
              {
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.d("camera initied");
                }
                InstantCameraView.this.cameraSession.setInitied();
              }
            }
          }, new Runnable()
          {
            public void run()
            {
              InstantCameraView.this.cameraThread.setCurrentSession(InstantCameraView.this.cameraSession);
            }
          });
        }
      }
    });
  }
  
  private boolean initCamera()
  {
    boolean bool = false;
    Object localObject1 = CameraController.getInstance().getCameras();
    if (localObject1 == null) {}
    int i;
    label20:
    do
    {
      return bool;
      localObject2 = null;
      i = 0;
      localObject3 = localObject2;
      if (i < ((ArrayList)localObject1).size())
      {
        localObject3 = (CameraInfo)((ArrayList)localObject1).get(i);
        if (!((CameraInfo)localObject3).isFrontface()) {
          localObject2 = localObject3;
        }
        if (((!this.isFrontface) || (!((CameraInfo)localObject3).isFrontface())) && ((this.isFrontface) || (((CameraInfo)localObject3).isFrontface()))) {
          break;
        }
        this.selectedCamera = ((CameraInfo)localObject3);
        localObject3 = localObject2;
      }
      if (this.selectedCamera == null) {
        this.selectedCamera = ((CameraInfo)localObject3);
      }
    } while (this.selectedCamera == null);
    Object localObject2 = this.selectedCamera.getPreviewSizes();
    Object localObject3 = this.selectedCamera.getPictureSizes();
    this.previewSize = CameraController.chooseOptimalSize((List)localObject2, 480, 270, this.aspectRatio);
    this.pictureSize = CameraController.chooseOptimalSize((List)localObject3, 480, 270, this.aspectRatio);
    int j;
    label195:
    int k;
    Size localSize;
    int m;
    if (this.previewSize.mWidth != this.pictureSize.mWidth)
    {
      i = 0;
      j = ((ArrayList)localObject2).size() - 1;
      k = i;
      if (j >= 0)
      {
        localSize = (Size)((ArrayList)localObject2).get(j);
        m = ((ArrayList)localObject3).size() - 1;
        label224:
        k = i;
        if (m >= 0)
        {
          localObject1 = (Size)((ArrayList)localObject3).get(m);
          if ((localSize.mWidth < this.pictureSize.mWidth) || (localSize.mHeight < this.pictureSize.mHeight) || (localSize.mWidth != ((Size)localObject1).mWidth) || (localSize.mHeight != ((Size)localObject1).mHeight)) {
            break label505;
          }
          this.previewSize = localSize;
          this.pictureSize = ((Size)localObject1);
          k = 1;
        }
        if (k == 0) {
          break label511;
        }
      }
      if (k == 0) {
        i = ((ArrayList)localObject2).size() - 1;
      }
    }
    for (;;)
    {
      if (i >= 0) {
        localSize = (Size)((ArrayList)localObject2).get(i);
      }
      for (m = ((ArrayList)localObject3).size() - 1;; m--)
      {
        j = k;
        if (m >= 0)
        {
          localObject1 = (Size)((ArrayList)localObject3).get(m);
          if ((localSize.mWidth >= 240) && (localSize.mHeight >= 240) && (localSize.mWidth == ((Size)localObject1).mWidth) && (localSize.mHeight == ((Size)localObject1).mHeight))
          {
            this.previewSize = localSize;
            this.pictureSize = ((Size)localObject1);
            j = 1;
          }
        }
        else
        {
          if (j == 0) {
            break label527;
          }
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("preview w = " + this.previewSize.mWidth + " h = " + this.previewSize.mHeight);
          }
          bool = true;
          break;
          localObject2 = localObject3;
          i++;
          break label20;
          label505:
          m--;
          break label224;
          label511:
          j--;
          i = k;
          break label195;
        }
      }
      label527:
      i--;
      k = j;
    }
  }
  
  private int loadShader(int paramInt, String paramString)
  {
    int i = GLES20.glCreateShader(paramInt);
    GLES20.glShaderSource(i, paramString);
    GLES20.glCompileShader(i);
    paramString = new int[1];
    GLES20.glGetShaderiv(i, 35713, paramString, 0);
    paramInt = i;
    if (paramString[0] == 0)
    {
      if (BuildVars.LOGS_ENABLED) {
        FileLog.e(GLES20.glGetShaderInfoLog(i));
      }
      GLES20.glDeleteShader(i);
      paramInt = 0;
    }
    return paramInt;
  }
  
  private void startProgressTimer()
  {
    if (this.progressTimer != null) {}
    try
    {
      this.progressTimer.cancel();
      this.progressTimer = null;
      this.progressTimer = new Timer();
      this.progressTimer.schedule(new TimerTask()
      {
        public void run()
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              long l = 0L;
              try
              {
                if ((InstantCameraView.this.videoPlayer != null) && (InstantCameraView.this.videoEditedInfo != null) && (InstantCameraView.this.videoEditedInfo.endTime > 0L) && (InstantCameraView.this.videoPlayer.getCurrentPosition() >= InstantCameraView.this.videoEditedInfo.endTime))
                {
                  VideoPlayer localVideoPlayer = InstantCameraView.this.videoPlayer;
                  if (InstantCameraView.this.videoEditedInfo.startTime > 0L) {
                    l = InstantCameraView.this.videoEditedInfo.startTime;
                  }
                  localVideoPlayer.seekTo(l);
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
          });
        }
      }, 0L, 17L);
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
  
  private void stopProgressTimer()
  {
    if (this.progressTimer != null) {}
    try
    {
      this.progressTimer.cancel();
      this.progressTimer = null;
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
  
  private void switchCamera()
  {
    if (this.cameraSession != null)
    {
      this.cameraSession.destroy();
      CameraController.getInstance().close(this.cameraSession, null, null);
      this.cameraSession = null;
    }
    if (!this.isFrontface) {}
    for (boolean bool = true;; bool = false)
    {
      this.isFrontface = bool;
      initCamera();
      this.cameraReady = false;
      this.cameraThread.reinitForNewCamera();
      return;
    }
  }
  
  public void cancel()
  {
    stopProgressTimer();
    if (this.videoPlayer != null)
    {
      this.videoPlayer.releasePlayer();
      this.videoPlayer = null;
    }
    if (this.textureView == null) {}
    for (;;)
    {
      return;
      this.cancelled = true;
      this.recording = false;
      AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recordStopped, new Object[] { Integer.valueOf(0) });
      if (this.cameraThread != null)
      {
        this.cameraThread.shutdown(0);
        this.cameraThread = null;
      }
      if (this.cameraFile != null)
      {
        this.cameraFile.delete();
        this.cameraFile = null;
      }
      startAnimation(false);
    }
  }
  
  public void changeVideoPreviewState(int paramInt, float paramFloat)
  {
    if (this.videoPlayer == null) {}
    for (;;)
    {
      return;
      if (paramInt == 0)
      {
        startProgressTimer();
        this.videoPlayer.play();
      }
      else if (paramInt == 1)
      {
        stopProgressTimer();
        this.videoPlayer.pause();
      }
      else if (paramInt == 2)
      {
        this.videoPlayer.seekTo(((float)this.videoPlayer.getDuration() * paramFloat));
      }
    }
  }
  
  public void destroy(boolean paramBoolean, Runnable paramRunnable)
  {
    CameraController localCameraController;
    CameraSession localCameraSession;
    if (this.cameraSession != null)
    {
      this.cameraSession.destroy();
      localCameraController = CameraController.getInstance();
      localCameraSession = this.cameraSession;
      if (paramBoolean) {
        break label48;
      }
    }
    label48:
    for (CountDownLatch localCountDownLatch = new CountDownLatch(1);; localCountDownLatch = null)
    {
      localCameraController.close(localCameraSession, localCountDownLatch, paramRunnable);
      return;
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.recordProgressChanged)
    {
      long l = ((Long)paramVarArgs[0]).longValue();
      this.progress = ((float)l / 60000.0F);
      this.recordedTime = l;
      invalidate();
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.FileDidUpload)
      {
        String str = (String)paramVarArgs[0];
        if ((this.cameraFile != null) && (this.cameraFile.getAbsolutePath().equals(str)))
        {
          this.file = ((TLRPC.InputFile)paramVarArgs[1]);
          this.encryptedFile = ((TLRPC.InputEncryptedFile)paramVarArgs[2]);
          this.size = ((Long)paramVarArgs[5]).longValue();
          if (this.encryptedFile != null)
          {
            this.key = ((byte[])paramVarArgs[3]);
            this.iv = ((byte[])paramVarArgs[4]);
          }
        }
      }
    }
  }
  
  public FrameLayout getCameraContainer()
  {
    return this.cameraContainer;
  }
  
  public Rect getCameraRect()
  {
    this.cameraContainer.getLocationOnScreen(this.position);
    return new Rect(this.position[0], this.position[1], this.cameraContainer.getWidth(), this.cameraContainer.getHeight());
  }
  
  public View getMuteImageView()
  {
    return this.muteImageView;
  }
  
  public Paint getPaint()
  {
    return this.paint;
  }
  
  public View getSwitchButtonView()
  {
    return this.switchCameraButton;
  }
  
  public void hideCamera(boolean paramBoolean)
  {
    destroy(paramBoolean, null);
    this.cameraContainer.removeView(this.textureView);
    this.cameraContainer.setTranslationX(0.0F);
    this.cameraContainer.setTranslationY(0.0F);
    this.textureView = null;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordProgressChanged);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    float f1 = this.cameraContainer.getX();
    float f2 = this.cameraContainer.getY();
    this.rect.set(f1 - AndroidUtilities.dp(8.0F), f2 - AndroidUtilities.dp(8.0F), this.cameraContainer.getMeasuredWidth() + f1 + AndroidUtilities.dp(8.0F), this.cameraContainer.getMeasuredHeight() + f2 + AndroidUtilities.dp(8.0F));
    if (this.progress != 0.0F) {
      paramCanvas.drawArc(this.rect, -90.0F, this.progress * 360.0F, false, this.paint);
    }
    if (Theme.chat_roundVideoShadow != null)
    {
      int i = (int)f1 - AndroidUtilities.dp(3.0F);
      int j = (int)f2 - AndroidUtilities.dp(2.0F);
      paramCanvas.save();
      paramCanvas.scale(this.cameraContainer.getScaleX(), this.cameraContainer.getScaleY(), AndroidUtilities.roundMessageSize / 2 + i + AndroidUtilities.dp(3.0F), AndroidUtilities.roundMessageSize / 2 + j + AndroidUtilities.dp(3.0F));
      Theme.chat_roundVideoShadow.setAlpha((int)(this.cameraContainer.getAlpha() * 255.0F));
      Theme.chat_roundVideoShadow.setBounds(i, j, AndroidUtilities.roundMessageSize + i + AndroidUtilities.dp(6.0F), AndroidUtilities.roundMessageSize + j + AndroidUtilities.dp(6.0F));
      Theme.chat_roundVideoShadow.draw(paramCanvas);
      paramCanvas.restore();
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    getParent().requestDisallowInterceptTouchEvent(true);
    return super.onInterceptTouchEvent(paramMotionEvent);
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (getVisibility() != 0) {
      this.cameraContainer.setTranslationY(getMeasuredHeight() / 2);
    }
  }
  
  public void send(int paramInt)
  {
    if (this.textureView == null) {
      return;
    }
    stopProgressTimer();
    if (this.videoPlayer != null)
    {
      this.videoPlayer.releasePlayer();
      this.videoPlayer = null;
    }
    label96:
    label117:
    Object localObject;
    if (paramInt == 4)
    {
      long l1;
      long l2;
      if (this.videoEditedInfo.needConvert())
      {
        this.file = null;
        this.encryptedFile = null;
        this.key = null;
        this.iv = null;
        double d = this.videoEditedInfo.estimatedDuration;
        if (this.videoEditedInfo.startTime >= 0L)
        {
          l1 = this.videoEditedInfo.startTime;
          if (this.videoEditedInfo.endTime < 0L) {
            break label328;
          }
          l2 = this.videoEditedInfo.endTime;
          this.videoEditedInfo.estimatedDuration = (l2 - l1);
          this.videoEditedInfo.estimatedSize = ((this.size * (this.videoEditedInfo.estimatedDuration / d)));
          this.videoEditedInfo.bitrate = 400000;
          if (this.videoEditedInfo.startTime > 0L)
          {
            localObject = this.videoEditedInfo;
            ((VideoEditedInfo)localObject).startTime *= 1000L;
          }
          if (this.videoEditedInfo.endTime > 0L)
          {
            localObject = this.videoEditedInfo;
            ((VideoEditedInfo)localObject).endTime *= 1000L;
          }
          FileLoader.getInstance(this.currentAccount).cancelUploadFile(this.cameraFile.getAbsolutePath(), false);
        }
      }
      for (;;)
      {
        this.videoEditedInfo.file = this.file;
        this.videoEditedInfo.encryptedFile = this.encryptedFile;
        this.videoEditedInfo.key = this.key;
        this.videoEditedInfo.iv = this.iv;
        this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0L, this.cameraFile.getAbsolutePath(), 0, true), this.videoEditedInfo);
        break;
        l1 = 0L;
        break label96;
        label328:
        l2 = this.videoEditedInfo.estimatedDuration;
        break label117;
        this.videoEditedInfo.estimatedSize = this.size;
      }
    }
    boolean bool;
    label368:
    int j;
    if (this.recordedTime < 800L)
    {
      bool = true;
      this.cancelled = bool;
      this.recording = false;
      AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
      if (this.cameraThread != null)
      {
        localObject = NotificationCenter.getInstance(this.currentAccount);
        int i = NotificationCenter.recordStopped;
        if ((this.cancelled) || (paramInt != 3)) {
          break label484;
        }
        j = 2;
        label422:
        ((NotificationCenter)localObject).postNotificationName(i, new Object[] { Integer.valueOf(j) });
        if (!this.cancelled) {
          break label490;
        }
        paramInt = 0;
      }
    }
    for (;;)
    {
      this.cameraThread.shutdown(paramInt);
      this.cameraThread = null;
      if (!this.cancelled) {
        break;
      }
      startAnimation(false);
      break;
      bool = false;
      break label368;
      label484:
      j = 0;
      break label422;
      label490:
      if (paramInt == 3) {
        paramInt = 2;
      } else {
        paramInt = 1;
      }
    }
  }
  
  @Keep
  public void setAlpha(float paramFloat)
  {
    ((ColorDrawable)getBackground()).setAlpha((int)(192.0F * paramFloat));
    invalidate();
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    setAlpha(0.0F);
    this.switchCameraButton.setAlpha(0.0F);
    this.cameraContainer.setAlpha(0.0F);
    this.muteImageView.setAlpha(0.0F);
    this.muteImageView.setScaleX(1.0F);
    this.muteImageView.setScaleY(1.0F);
    this.cameraContainer.setScaleX(0.1F);
    this.cameraContainer.setScaleY(0.1F);
    if (this.cameraContainer.getMeasuredWidth() != 0)
    {
      this.cameraContainer.setPivotX(this.cameraContainer.getMeasuredWidth() / 2);
      this.cameraContainer.setPivotY(this.cameraContainer.getMeasuredHeight() / 2);
    }
    if (paramInt == 0) {}
    try
    {
      ((Activity)getContext()).getWindow().addFlags(128);
      for (;;)
      {
        return;
        ((Activity)getContext()).getWindow().clearFlags(128);
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public void showCamera()
  {
    if (this.textureView != null) {}
    for (;;)
    {
      return;
      this.switchCameraButton.setImageResource(NUM);
      this.isFrontface = true;
      this.selectedCamera = null;
      this.recordedTime = 0L;
      this.progress = 0.0F;
      this.cancelled = false;
      this.file = null;
      this.encryptedFile = null;
      this.key = null;
      this.iv = null;
      if (initCamera())
      {
        MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
        this.cameraFile = new File(FileLoader.getDirectory(4), SharedConfig.getLastLocalId() + ".mp4");
        SharedConfig.saveConfig();
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("show round camera");
        }
        this.textureView = new TextureView(getContext());
        this.textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
        {
          public void onSurfaceTextureAvailable(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2)
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("camera surface available");
            }
            if ((InstantCameraView.this.cameraThread != null) || (paramAnonymousSurfaceTexture == null) || (InstantCameraView.this.cancelled)) {}
            for (;;)
            {
              return;
              if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start create thread");
              }
              InstantCameraView.access$1102(InstantCameraView.this, new InstantCameraView.CameraGLThread(InstantCameraView.this, paramAnonymousSurfaceTexture, paramAnonymousInt1, paramAnonymousInt2));
            }
          }
          
          public boolean onSurfaceTextureDestroyed(SurfaceTexture paramAnonymousSurfaceTexture)
          {
            if (InstantCameraView.this.cameraThread != null)
            {
              InstantCameraView.this.cameraThread.shutdown(0);
              InstantCameraView.access$1102(InstantCameraView.this, null);
            }
            if (InstantCameraView.this.cameraSession != null) {
              CameraController.getInstance().close(InstantCameraView.this.cameraSession, null, null);
            }
            return true;
          }
          
          public void onSurfaceTextureSizeChanged(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2) {}
          
          public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymousSurfaceTexture) {}
        });
        this.cameraContainer.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0F));
        setVisibility(0);
        startAnimation(true);
      }
    }
  }
  
  public void startAnimation(boolean paramBoolean)
  {
    float f1 = 1.0F;
    float f2 = 0.0F;
    if (this.animatorSet != null) {
      this.animatorSet.cancel();
    }
    Object localObject1 = PipRoundVideoView.getInstance();
    boolean bool;
    float f3;
    label66:
    ObjectAnimator localObjectAnimator1;
    Object localObject2;
    label96:
    ObjectAnimator localObjectAnimator2;
    Object localObject3;
    int i;
    label148:
    Object localObject4;
    label179:
    Object localObject5;
    label210:
    Object localObject6;
    label241:
    FrameLayout localFrameLayout;
    if (localObject1 != null)
    {
      if (!paramBoolean)
      {
        bool = true;
        ((PipRoundVideoView)localObject1).showTemporary(bool);
      }
    }
    else
    {
      this.animatorSet = new AnimatorSet();
      localObject1 = this.animatorSet;
      if (!paramBoolean) {
        break label410;
      }
      f3 = 1.0F;
      localObjectAnimator1 = ObjectAnimator.ofFloat(this, "alpha", new float[] { f3 });
      localObject2 = this.switchCameraButton;
      if (!paramBoolean) {
        break label416;
      }
      f3 = 1.0F;
      localObjectAnimator2 = ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f3 });
      localObject2 = ObjectAnimator.ofFloat(this.muteImageView, "alpha", new float[] { 0.0F });
      localObject3 = this.paint;
      if (!paramBoolean) {
        break label422;
      }
      i = 255;
      localObject3 = ObjectAnimator.ofInt(localObject3, "alpha", new int[] { i });
      localObject4 = this.cameraContainer;
      if (!paramBoolean) {
        break label428;
      }
      f3 = 1.0F;
      localObject4 = ObjectAnimator.ofFloat(localObject4, "alpha", new float[] { f3 });
      localObject5 = this.cameraContainer;
      if (!paramBoolean) {
        break label434;
      }
      f3 = 1.0F;
      localObject5 = ObjectAnimator.ofFloat(localObject5, "scaleX", new float[] { f3 });
      localObject6 = this.cameraContainer;
      if (!paramBoolean) {
        break label442;
      }
      f3 = f1;
      localObject6 = ObjectAnimator.ofFloat(localObject6, "scaleY", new float[] { f3 });
      localFrameLayout = this.cameraContainer;
      if (!paramBoolean) {
        break label450;
      }
      f3 = getMeasuredHeight() / 2;
      label278:
      if (!paramBoolean) {
        break label456;
      }
    }
    for (;;)
    {
      ((AnimatorSet)localObject1).playTogether(new Animator[] { localObjectAnimator1, localObjectAnimator2, localObject2, localObject3, localObject4, localObject5, localObject6, ObjectAnimator.ofFloat(localFrameLayout, "translationY", new float[] { f3, f2 }) });
      if (!paramBoolean) {
        this.animatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if (paramAnonymousAnimator.equals(InstantCameraView.this.animatorSet))
            {
              InstantCameraView.this.hideCamera(true);
              InstantCameraView.this.setVisibility(4);
            }
          }
        });
      }
      this.animatorSet.setDuration(180L);
      this.animatorSet.setInterpolator(new DecelerateInterpolator());
      this.animatorSet.start();
      return;
      bool = false;
      break;
      label410:
      f3 = 0.0F;
      break label66;
      label416:
      f3 = 0.0F;
      break label96;
      label422:
      i = 0;
      break label148;
      label428:
      f3 = 0.0F;
      break label179;
      label434:
      f3 = 0.1F;
      break label210;
      label442:
      f3 = 0.1F;
      break label241;
      label450:
      f3 = 0.0F;
      break label278;
      label456:
      f2 = getMeasuredHeight() / 2;
    }
  }
  
  private class AudioBufferInfo
  {
    byte[] buffer = new byte['倀'];
    boolean last;
    int lastWroteBuffer;
    long[] offset = new long[10];
    int[] read = new int[10];
    int results;
    
    private AudioBufferInfo() {}
  }
  
  public class CameraGLThread
    extends DispatchQueue
  {
    private final int DO_REINIT_MESSAGE = 2;
    private final int DO_RENDER_MESSAGE = 0;
    private final int DO_SETSESSION_MESSAGE = 3;
    private final int DO_SHUTDOWN_MESSAGE = 1;
    private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private final int EGL_OPENGL_ES2_BIT = 4;
    private Integer cameraId = Integer.valueOf(0);
    private SurfaceTexture cameraSurface;
    private CameraSession currentSession;
    private int drawProgram;
    private EGL10 egl10;
    private javax.microedition.khronos.egl.EGLConfig eglConfig;
    private javax.microedition.khronos.egl.EGLContext eglContext;
    private javax.microedition.khronos.egl.EGLDisplay eglDisplay;
    private javax.microedition.khronos.egl.EGLSurface eglSurface;
    private GL gl;
    private boolean initied;
    private int positionHandle;
    private boolean recording;
    private int rotationAngle;
    private SurfaceTexture surfaceTexture;
    private int textureHandle;
    private int textureMatrixHandle;
    private int vertexMatrixHandle;
    private InstantCameraView.VideoRecorder videoEncoder;
    
    public CameraGLThread(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
    {
      super();
      this.surfaceTexture = paramSurfaceTexture;
      int i = InstantCameraView.this.previewSize.getWidth();
      int j = InstantCameraView.this.previewSize.getHeight();
      float f = paramInt1 / Math.min(i, j);
      i = (int)(i * f);
      j = (int)(j * f);
      if (i > j)
      {
        InstantCameraView.access$2102(InstantCameraView.this, 1.0F);
        InstantCameraView.access$2202(InstantCameraView.this, i / paramInt2);
      }
      for (;;)
      {
        return;
        InstantCameraView.access$2102(InstantCameraView.this, j / paramInt1);
        InstantCameraView.access$2202(InstantCameraView.this, 1.0F);
      }
    }
    
    private boolean initGL()
    {
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("start init gl");
      }
      this.egl10 = ((EGL10)javax.microedition.khronos.egl.EGLContext.getEGL());
      this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
      boolean bool;
      if (this.eglDisplay == EGL10.EGL_NO_DISPLAY)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
        }
        finish();
        bool = false;
      }
      for (;;)
      {
        return bool;
        Object localObject1 = new int[2];
        if (!this.egl10.eglInitialize(this.eglDisplay, (int[])localObject1))
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
          }
          finish();
          bool = false;
        }
        else
        {
          localObject1 = new int[1];
          Object localObject2 = new javax.microedition.khronos.egl.EGLConfig[1];
          if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[] { 12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12344 }, (javax.microedition.khronos.egl.EGLConfig[])localObject2, 1, (int[])localObject1))
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
            }
            finish();
            bool = false;
          }
          else
          {
            if (localObject1[0] > 0)
            {
              this.eglConfig = localObject2[0];
              this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[] { 12440, 2, 12344 });
              if (this.eglContext == null)
              {
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                bool = false;
              }
            }
            else
            {
              if (BuildVars.LOGS_ENABLED) {
                FileLog.e("eglConfig not initialized");
              }
              finish();
              bool = false;
              continue;
            }
            if ((this.surfaceTexture instanceof SurfaceTexture))
            {
              this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surfaceTexture, null);
              if ((this.eglSurface == null) || (this.eglSurface == EGL10.EGL_NO_SURFACE))
              {
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                bool = false;
              }
            }
            else
            {
              finish();
              bool = false;
              continue;
            }
            if (!this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext))
            {
              if (BuildVars.LOGS_ENABLED) {
                FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
              }
              finish();
              bool = false;
            }
            else
            {
              this.gl = this.eglContext.getGL();
              float f1 = 1.0F / InstantCameraView.this.scaleX / 2.0F;
              float f2 = 1.0F / InstantCameraView.this.scaleY / 2.0F;
              localObject2 = new float[12];
              Object tmp691_690 = localObject2;
              tmp691_690[0] = -1.0F;
              Object tmp696_691 = tmp691_690;
              tmp696_691[1] = -1.0F;
              Object tmp701_696 = tmp696_691;
              tmp701_696[2] = 0.0F;
              Object tmp705_701 = tmp701_696;
              tmp705_701[3] = 1.0F;
              Object tmp709_705 = tmp705_701;
              tmp709_705[4] = -1.0F;
              Object tmp714_709 = tmp709_705;
              tmp714_709[5] = 0.0F;
              Object tmp718_714 = tmp714_709;
              tmp718_714[6] = -1.0F;
              Object tmp724_718 = tmp718_714;
              tmp724_718[7] = 1.0F;
              Object tmp729_724 = tmp724_718;
              tmp729_724[8] = 0.0F;
              Object tmp734_729 = tmp729_724;
              tmp734_729[9] = 1.0F;
              Object tmp739_734 = tmp734_729;
              tmp739_734[10] = 1.0F;
              Object tmp744_739 = tmp739_734;
              tmp744_739[11] = 0.0F;
              tmp744_739;
              localObject1 = new float[8];
              localObject1[0] = (0.5F - f1);
              localObject1[1] = (0.5F - f2);
              localObject1[2] = (0.5F + f1);
              localObject1[3] = (0.5F - f2);
              localObject1[4] = (0.5F - f1);
              localObject1[5] = (0.5F + f2);
              localObject1[6] = (0.5F + f1);
              localObject1[7] = (0.5F + f2);
              this.videoEncoder = new InstantCameraView.VideoRecorder(InstantCameraView.this, null);
              InstantCameraView.access$2402(InstantCameraView.this, ByteBuffer.allocateDirect(localObject2.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer());
              InstantCameraView.this.vertexBuffer.put((float[])localObject2).position(0);
              InstantCameraView.access$2502(InstantCameraView.this, ByteBuffer.allocateDirect(localObject1.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer());
              InstantCameraView.this.textureBuffer.put((float[])localObject1).position(0);
              Matrix.setIdentityM(InstantCameraView.this.mSTMatrix, 0);
              int i = InstantCameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
              int j = InstantCameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
              if ((i != 0) && (j != 0))
              {
                this.drawProgram = GLES20.glCreateProgram();
                GLES20.glAttachShader(this.drawProgram, i);
                GLES20.glAttachShader(this.drawProgram, j);
                GLES20.glLinkProgram(this.drawProgram);
                localObject1 = new int[1];
                GLES20.glGetProgramiv(this.drawProgram, 35714, (int[])localObject1, 0);
                if (localObject1[0] == 0)
                {
                  if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("failed link shader");
                  }
                  GLES20.glDeleteProgram(this.drawProgram);
                  this.drawProgram = 0;
                }
                for (;;)
                {
                  GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
                  GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                  GLES20.glTexParameteri(36197, 10241, 9729);
                  GLES20.glTexParameteri(36197, 10240, 9729);
                  GLES20.glTexParameteri(36197, 10242, 33071);
                  GLES20.glTexParameteri(36197, 10243, 33071);
                  Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
                  this.cameraSurface = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                  this.cameraSurface.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener()
                  {
                    public void onFrameAvailable(SurfaceTexture paramAnonymousSurfaceTexture)
                    {
                      InstantCameraView.CameraGLThread.this.requestRender();
                    }
                  });
                  InstantCameraView.this.createCamera(this.cameraSurface);
                  if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("gl initied");
                  }
                  bool = true;
                  break;
                  this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
                  this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
                  this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                  this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                }
              }
              if (BuildVars.LOGS_ENABLED) {
                FileLog.e("failed creating shader");
              }
              finish();
              bool = false;
            }
          }
        }
      }
    }
    
    private void onDraw(Integer paramInteger)
    {
      if (!this.initied) {}
      for (;;)
      {
        return;
        if (((!this.eglContext.equals(this.egl10.eglGetCurrentContext())) || (!this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377)))) && (!this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)))
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
          }
        }
        else
        {
          this.cameraSurface.updateTexImage();
          if (!this.recording)
          {
            this.videoEncoder.startRecording(InstantCameraView.this.cameraFile, EGL14.eglGetCurrentContext());
            this.recording = true;
            int i = this.currentSession.getCurrentOrientation();
            if ((i == 90) || (i == 270))
            {
              float f = InstantCameraView.this.scaleX;
              InstantCameraView.access$2102(InstantCameraView.this, InstantCameraView.this.scaleY);
              InstantCameraView.access$2202(InstantCameraView.this, f);
            }
          }
          this.videoEncoder.frameAvailable(this.cameraSurface, paramInteger, System.nanoTime());
          this.cameraSurface.getTransformMatrix(InstantCameraView.this.mSTMatrix);
          GLES20.glUseProgram(this.drawProgram);
          GLES20.glActiveTexture(33984);
          GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
          GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, InstantCameraView.this.vertexBuffer);
          GLES20.glEnableVertexAttribArray(this.positionHandle);
          GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, InstantCameraView.this.textureBuffer);
          GLES20.glEnableVertexAttribArray(this.textureHandle);
          GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.mSTMatrix, 0);
          GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, InstantCameraView.this.mMVPMatrix, 0);
          GLES20.glDrawArrays(5, 0, 4);
          GLES20.glDisableVertexAttribArray(this.positionHandle);
          GLES20.glDisableVertexAttribArray(this.textureHandle);
          GLES20.glBindTexture(36197, 0);
          GLES20.glUseProgram(0);
          this.egl10.eglSwapBuffers(this.eglDisplay, this.eglSurface);
        }
      }
    }
    
    public void finish()
    {
      if (this.eglSurface != null)
      {
        this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
        this.eglSurface = null;
      }
      if (this.eglContext != null)
      {
        this.egl10.eglDestroyContext(this.eglDisplay, this.eglContext);
        this.eglContext = null;
      }
      if (this.eglDisplay != null)
      {
        this.egl10.eglTerminate(this.eglDisplay);
        this.eglDisplay = null;
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      }
      for (;;)
      {
        return;
        onDraw((Integer)paramMessage.obj);
        continue;
        finish();
        if (this.recording) {
          this.videoEncoder.stopRecording(paramMessage.arg1);
        }
        paramMessage = Looper.myLooper();
        if (paramMessage != null)
        {
          paramMessage.quit();
          continue;
          if (!this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext))
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
            }
          }
          else
          {
            if (this.cameraSurface != null)
            {
              this.cameraSurface.getTransformMatrix(InstantCameraView.this.moldSTMatrix);
              this.cameraSurface.setOnFrameAvailableListener(null);
              this.cameraSurface.release();
              InstantCameraView.this.oldCameraTexture[0] = InstantCameraView.this.cameraTexture[0];
              InstantCameraView.access$3402(InstantCameraView.this, 0.0F);
              InstantCameraView.this.cameraTexture[0] = 0;
            }
            paramMessage = this.cameraId;
            this.cameraId = Integer.valueOf(this.cameraId.intValue() + 1);
            InstantCameraView.access$902(InstantCameraView.this, false);
            GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
            GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
            GLES20.glTexParameteri(36197, 10241, 9729);
            GLES20.glTexParameteri(36197, 10240, 9729);
            GLES20.glTexParameteri(36197, 10242, 33071);
            GLES20.glTexParameteri(36197, 10243, 33071);
            this.cameraSurface = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
            this.cameraSurface.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener()
            {
              public void onFrameAvailable(SurfaceTexture paramAnonymousSurfaceTexture)
              {
                InstantCameraView.CameraGLThread.this.requestRender();
              }
            });
            InstantCameraView.this.createCamera(this.cameraSurface);
            continue;
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("set gl rednderer session");
            }
            paramMessage = (CameraSession)paramMessage.obj;
            if (this.currentSession == paramMessage)
            {
              this.rotationAngle = this.currentSession.getWorldAngle();
              Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
              if (this.rotationAngle != 0) {
                Matrix.rotateM(InstantCameraView.this.mMVPMatrix, 0, this.rotationAngle, 0.0F, 0.0F, 1.0F);
              }
            }
            else
            {
              this.currentSession = paramMessage;
            }
          }
        }
      }
    }
    
    public void reinitForNewCamera()
    {
      Handler localHandler = InstantCameraView.this.getHandler();
      if (localHandler != null) {
        sendMessage(localHandler.obtainMessage(2), 0);
      }
    }
    
    public void requestRender()
    {
      Handler localHandler = InstantCameraView.this.getHandler();
      if (localHandler != null) {
        sendMessage(localHandler.obtainMessage(0, this.cameraId), 0);
      }
    }
    
    public void run()
    {
      this.initied = initGL();
      super.run();
    }
    
    public void setCurrentSession(CameraSession paramCameraSession)
    {
      Handler localHandler = InstantCameraView.this.getHandler();
      if (localHandler != null) {
        sendMessage(localHandler.obtainMessage(3, paramCameraSession), 0);
      }
    }
    
    public void shutdown(int paramInt)
    {
      Handler localHandler = InstantCameraView.this.getHandler();
      if (localHandler != null) {
        sendMessage(localHandler.obtainMessage(1, paramInt, 0), 0);
      }
    }
  }
  
  private static class EncoderHandler
    extends Handler
  {
    private WeakReference<InstantCameraView.VideoRecorder> mWeakEncoder;
    
    public EncoderHandler(InstantCameraView.VideoRecorder paramVideoRecorder)
    {
      this.mWeakEncoder = new WeakReference(paramVideoRecorder);
    }
    
    public void exit()
    {
      Looper.myLooper().quit();
    }
    
    public void handleMessage(Message paramMessage)
    {
      int i = paramMessage.what;
      Object localObject = paramMessage.obj;
      localObject = (InstantCameraView.VideoRecorder)this.mWeakEncoder.get();
      if (localObject == null) {}
      for (;;)
      {
        return;
        switch (i)
        {
        default: 
          break;
        case 0: 
          try
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.e("start encoder");
            }
            InstantCameraView.VideoRecorder.access$3500((InstantCameraView.VideoRecorder)localObject);
          }
          catch (Exception paramMessage)
          {
            FileLog.e(paramMessage);
            InstantCameraView.VideoRecorder.access$3600((InstantCameraView.VideoRecorder)localObject, 0);
            Looper.myLooper().quit();
          }
          break;
        case 1: 
          if (BuildVars.LOGS_ENABLED) {
            FileLog.e("stop encoder");
          }
          InstantCameraView.VideoRecorder.access$3600((InstantCameraView.VideoRecorder)localObject, paramMessage.arg1);
          break;
        case 2: 
          InstantCameraView.VideoRecorder.access$3700((InstantCameraView.VideoRecorder)localObject, paramMessage.arg1 << 32 | paramMessage.arg2 & 0xFFFFFFFF, (Integer)paramMessage.obj);
          break;
        case 3: 
          InstantCameraView.VideoRecorder.access$3800((InstantCameraView.VideoRecorder)localObject, (InstantCameraView.AudioBufferInfo)paramMessage.obj);
        }
      }
    }
  }
  
  private class VideoRecorder
    implements Runnable
  {
    private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
    private static final int FRAME_RATE = 30;
    private static final int IFRAME_INTERVAL = 1;
    private static final String VIDEO_MIME_TYPE = "video/avc";
    private int alphaHandle;
    private MediaCodec.BufferInfo audioBufferInfo;
    private MediaCodec audioEncoder;
    private long audioFirst = -1L;
    private AudioRecord audioRecorder;
    private long audioStartTime = -1L;
    private boolean audioStopedByTime;
    private int audioTrackIndex = -5;
    private boolean blendEnabled;
    private ArrayBlockingQueue<InstantCameraView.AudioBufferInfo> buffers = new ArrayBlockingQueue(10);
    private ArrayList<InstantCameraView.AudioBufferInfo> buffersToWrite = new ArrayList();
    private long currentTimestamp = 0L;
    private long desyncTime;
    private int drawProgram;
    private android.opengl.EGLConfig eglConfig;
    private android.opengl.EGLContext eglContext = EGL14.EGL_NO_CONTEXT;
    private android.opengl.EGLDisplay eglDisplay = EGL14.EGL_NO_DISPLAY;
    private android.opengl.EGLSurface eglSurface = EGL14.EGL_NO_SURFACE;
    private volatile InstantCameraView.EncoderHandler handler;
    private Integer lastCameraId = Integer.valueOf(0);
    private long lastCommitedFrameTime;
    private long lastTimestamp = -1L;
    private MP4Builder mediaMuxer;
    private int positionHandle;
    private boolean ready;
    private Runnable recorderRunnable = new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: ldc2_w 25
        //   3: lstore_1
        //   4: iconst_0
        //   5: istore_3
        //   6: iload_3
        //   7: ifne +56 -> 63
        //   10: iload_3
        //   11: istore 4
        //   13: aload_0
        //   14: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   17: invokestatic 30	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$3900	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Z
        //   20: ifne +91 -> 111
        //   23: iload_3
        //   24: istore 4
        //   26: aload_0
        //   27: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   30: invokestatic 34	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4000	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Landroid/media/AudioRecord;
        //   33: invokevirtual 40	android/media/AudioRecord:getRecordingState	()I
        //   36: iconst_1
        //   37: if_icmpeq +74 -> 111
        //   40: aload_0
        //   41: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   44: invokestatic 34	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4000	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Landroid/media/AudioRecord;
        //   47: invokevirtual 43	android/media/AudioRecord:stop	()V
        //   50: iload_3
        //   51: istore 4
        //   53: aload_0
        //   54: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   57: invokestatic 47	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4100	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)I
        //   60: ifne +51 -> 111
        //   63: aload_0
        //   64: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   67: invokestatic 34	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4000	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Landroid/media/AudioRecord;
        //   70: invokevirtual 50	android/media/AudioRecord:release	()V
        //   73: aload_0
        //   74: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   77: invokestatic 54	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4400	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Lorg/telegram/ui/Components/InstantCameraView$EncoderHandler;
        //   80: aload_0
        //   81: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   84: invokestatic 54	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4400	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Lorg/telegram/ui/Components/InstantCameraView$EncoderHandler;
        //   87: iconst_1
        //   88: aload_0
        //   89: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   92: invokestatic 47	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4100	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)I
        //   95: iconst_0
        //   96: invokevirtual 60	org/telegram/ui/Components/InstantCameraView$EncoderHandler:obtainMessage	(III)Landroid/os/Message;
        //   99: invokevirtual 64	org/telegram/ui/Components/InstantCameraView$EncoderHandler:sendMessage	(Landroid/os/Message;)Z
        //   102: pop
        //   103: return
        //   104: astore 5
        //   106: iconst_1
        //   107: istore_3
        //   108: goto -58 -> 50
        //   111: aload_0
        //   112: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   115: invokestatic 68	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4200	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Ljava/util/concurrent/ArrayBlockingQueue;
        //   118: invokevirtual 74	java/util/concurrent/ArrayBlockingQueue:isEmpty	()Z
        //   121: ifeq +198 -> 319
        //   124: new 76	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo
        //   127: dup
        //   128: aload_0
        //   129: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   132: getfield 80	org/telegram/ui/Components/InstantCameraView$VideoRecorder:this$0	Lorg/telegram/ui/Components/InstantCameraView;
        //   135: aconst_null
        //   136: invokespecial 83	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo:<init>	(Lorg/telegram/ui/Components/InstantCameraView;Lorg/telegram/ui/Components/InstantCameraView$1;)V
        //   139: astore 5
        //   141: aload 5
        //   143: iconst_0
        //   144: putfield 87	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo:lastWroteBuffer	I
        //   147: aload 5
        //   149: bipush 10
        //   151: putfield 90	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo:results	I
        //   154: iconst_0
        //   155: istore_3
        //   156: lload_1
        //   157: lstore 6
        //   159: iload_3
        //   160: bipush 10
        //   162: if_icmpge +83 -> 245
        //   165: lload_1
        //   166: lstore 8
        //   168: lload_1
        //   169: ldc2_w 25
        //   172: lcmp
        //   173: ifne +12 -> 185
        //   176: invokestatic 96	java/lang/System:nanoTime	()J
        //   179: ldc2_w 97
        //   182: ldiv
        //   183: lstore 8
        //   185: aload_0
        //   186: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   189: invokestatic 34	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4000	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Landroid/media/AudioRecord;
        //   192: aload 5
        //   194: getfield 102	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo:buffer	[B
        //   197: iload_3
        //   198: sipush 2048
        //   201: imul
        //   202: sipush 2048
        //   205: invokevirtual 106	android/media/AudioRecord:read	([BII)I
        //   208: istore 10
        //   210: iload 10
        //   212: ifgt +125 -> 337
        //   215: aload 5
        //   217: iload_3
        //   218: putfield 90	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo:results	I
        //   221: lload 8
        //   223: lstore 6
        //   225: aload_0
        //   226: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   229: invokestatic 30	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$3900	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Z
        //   232: ifne +13 -> 245
        //   235: aload 5
        //   237: iconst_1
        //   238: putfield 110	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo:last	Z
        //   241: lload 8
        //   243: lstore 6
        //   245: aload 5
        //   247: getfield 90	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo:results	I
        //   250: ifge +11 -> 261
        //   253: aload 5
        //   255: getfield 110	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo:last	Z
        //   258: ifeq +118 -> 376
        //   261: iload 4
        //   263: istore_3
        //   264: aload_0
        //   265: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   268: invokestatic 30	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$3900	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Z
        //   271: ifne +18 -> 289
        //   274: iload 4
        //   276: istore_3
        //   277: aload 5
        //   279: getfield 90	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo:results	I
        //   282: bipush 10
        //   284: if_icmpge +5 -> 289
        //   287: iconst_1
        //   288: istore_3
        //   289: aload_0
        //   290: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   293: invokestatic 54	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4400	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Lorg/telegram/ui/Components/InstantCameraView$EncoderHandler;
        //   296: aload_0
        //   297: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   300: invokestatic 54	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4400	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Lorg/telegram/ui/Components/InstantCameraView$EncoderHandler;
        //   303: iconst_3
        //   304: aload 5
        //   306: invokevirtual 113	org/telegram/ui/Components/InstantCameraView$EncoderHandler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
        //   309: invokevirtual 64	org/telegram/ui/Components/InstantCameraView$EncoderHandler:sendMessage	(Landroid/os/Message;)Z
        //   312: pop
        //   313: lload 6
        //   315: lstore_1
        //   316: goto -310 -> 6
        //   319: aload_0
        //   320: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   323: invokestatic 68	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4200	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Ljava/util/concurrent/ArrayBlockingQueue;
        //   326: invokevirtual 117	java/util/concurrent/ArrayBlockingQueue:poll	()Ljava/lang/Object;
        //   329: checkcast 76	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo
        //   332: astore 5
        //   334: goto -193 -> 141
        //   337: aload 5
        //   339: getfield 121	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo:offset	[J
        //   342: iload_3
        //   343: lload 8
        //   345: lastore
        //   346: aload 5
        //   348: getfield 124	org/telegram/ui/Components/InstantCameraView$AudioBufferInfo:read	[I
        //   351: iload_3
        //   352: iload 10
        //   354: iastore
        //   355: lload 8
        //   357: ldc 125
        //   359: iload 10
        //   361: imul
        //   362: ldc 126
        //   364: idiv
        //   365: iconst_2
        //   366: idiv
        //   367: i2l
        //   368: ladd
        //   369: lstore_1
        //   370: iinc 3 1
        //   373: goto -217 -> 156
        //   376: aload_0
        //   377: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   380: invokestatic 30	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$3900	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Z
        //   383: ifne +11 -> 394
        //   386: iconst_1
        //   387: istore_3
        //   388: lload 6
        //   390: lstore_1
        //   391: goto -385 -> 6
        //   394: aload_0
        //   395: getfield 17	org/telegram/ui/Components/InstantCameraView$VideoRecorder$1:this$1	Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;
        //   398: invokestatic 68	org/telegram/ui/Components/InstantCameraView$VideoRecorder:access$4200	(Lorg/telegram/ui/Components/InstantCameraView$VideoRecorder;)Ljava/util/concurrent/ArrayBlockingQueue;
        //   401: aload 5
        //   403: invokevirtual 130	java/util/concurrent/ArrayBlockingQueue:put	(Ljava/lang/Object;)V
        //   406: lload 6
        //   408: lstore_1
        //   409: iload 4
        //   411: istore_3
        //   412: goto -406 -> 6
        //   415: astore 5
        //   417: lload 6
        //   419: lstore_1
        //   420: iload 4
        //   422: istore_3
        //   423: goto -417 -> 6
        //   426: astore 5
        //   428: aload 5
        //   430: invokestatic 136	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   433: goto -360 -> 73
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	436	0	this	1
        //   3	417	1	l1	long
        //   5	418	3	i	int
        //   11	410	4	j	int
        //   104	1	5	localException1	Exception
        //   139	263	5	localAudioBufferInfo	InstantCameraView.AudioBufferInfo
        //   415	1	5	localException2	Exception
        //   426	3	5	localException3	Exception
        //   157	261	6	l2	long
        //   166	190	8	l3	long
        //   208	154	10	k	int
        // Exception table:
        //   from	to	target	type
        //   40	50	104	java/lang/Exception
        //   394	406	415	java/lang/Exception
        //   63	73	426	java/lang/Exception
      }
    };
    private volatile boolean running;
    private int scaleXHandle;
    private int scaleYHandle;
    private volatile int sendWhenDone;
    private android.opengl.EGLContext sharedEglContext;
    private boolean skippedFirst;
    private long skippedTime;
    private Surface surface;
    private final Object sync = new Object();
    private int textureHandle;
    private int textureMatrixHandle;
    private int vertexMatrixHandle;
    private int videoBitrate;
    private MediaCodec.BufferInfo videoBufferInfo;
    private boolean videoConvertFirstWrite = true;
    private MediaCodec videoEncoder;
    private File videoFile;
    private long videoFirst = -1L;
    private int videoHeight;
    private long videoLast;
    private int videoTrackIndex = -5;
    private int videoWidth;
    private int zeroTimeStamps;
    
    private VideoRecorder() {}
    
    private void didWriteData(File paramFile, boolean paramBoolean)
    {
      long l1 = 0L;
      Object localObject1;
      Object localObject2;
      boolean bool;
      long l2;
      if (this.videoConvertFirstWrite)
      {
        FileLoader.getInstance(InstantCameraView.this.currentAccount).uploadFile(paramFile.toString(), InstantCameraView.this.isSecretChat, false, 1, 33554432);
        this.videoConvertFirstWrite = false;
        if (paramBoolean)
        {
          localObject1 = FileLoader.getInstance(InstantCameraView.this.currentAccount);
          localObject2 = paramFile.toString();
          bool = InstantCameraView.this.isSecretChat;
          l2 = paramFile.length();
          if (paramBoolean) {
            l1 = paramFile.length();
          }
          ((FileLoader)localObject1).checkUploadNewDataAvailable((String)localObject2, bool, l2, l1);
        }
      }
      for (;;)
      {
        return;
        localObject2 = FileLoader.getInstance(InstantCameraView.this.currentAccount);
        localObject1 = paramFile.toString();
        bool = InstantCameraView.this.isSecretChat;
        l2 = paramFile.length();
        if (paramBoolean) {
          l1 = paramFile.length();
        }
        ((FileLoader)localObject2).checkUploadNewDataAvailable((String)localObject1, bool, l2, l1);
      }
    }
    
    private void handleAudioFrameAvailable(InstantCameraView.AudioBufferInfo paramAudioBufferInfo)
    {
      if (this.audioStopedByTime) {}
      for (;;)
      {
        return;
        this.buffersToWrite.add(paramAudioBufferInfo);
        Object localObject = paramAudioBufferInfo;
        label56:
        int i;
        int j;
        int k;
        if (this.audioFirst == -1L)
        {
          if (this.videoFirst == -1L)
          {
            if (!BuildVars.LOGS_ENABLED) {
              continue;
            }
            FileLog.d("video record not yet started");
            continue;
          }
          i = 0;
          for (j = 0;; j++)
          {
            k = i;
            if (j < paramAudioBufferInfo.results)
            {
              if ((j != 0) || (Math.abs(this.videoFirst - paramAudioBufferInfo.offset[j]) <= 100000000L)) {
                break label243;
              }
              this.desyncTime = (this.videoFirst - paramAudioBufferInfo.offset[j]);
              this.audioFirst = paramAudioBufferInfo.offset[j];
              j = 1;
              k = j;
              if (BuildVars.LOGS_ENABLED)
              {
                FileLog.d("detected desync between audio and video " + this.desyncTime);
                k = j;
              }
            }
            for (;;)
            {
              localObject = paramAudioBufferInfo;
              if (k != 0) {
                break label384;
              }
              if (BuildVars.LOGS_ENABLED) {
                FileLog.d("first audio frame not found, removing buffers " + paramAudioBufferInfo.results);
              }
              this.buffersToWrite.remove(paramAudioBufferInfo);
              if (this.buffersToWrite.isEmpty()) {
                break;
              }
              paramAudioBufferInfo = (InstantCameraView.AudioBufferInfo)this.buffersToWrite.get(0);
              break label56;
              label243:
              if (paramAudioBufferInfo.offset[j] < this.videoFirst) {
                break label332;
              }
              paramAudioBufferInfo.lastWroteBuffer = j;
              this.audioFirst = paramAudioBufferInfo.offset[j];
              i = 1;
              k = i;
              if (BuildVars.LOGS_ENABLED)
              {
                FileLog.d("found first audio frame at " + j + " timestamp = " + paramAudioBufferInfo.offset[j]);
                k = i;
              }
            }
            label332:
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("ignore first audio frame at " + j + " timestamp = " + paramAudioBufferInfo.offset[j]);
            }
          }
        }
        label384:
        if (this.audioStartTime == -1L) {
          this.audioStartTime = localObject.offset[localObject.lastWroteBuffer];
        }
        paramAudioBufferInfo = (InstantCameraView.AudioBufferInfo)localObject;
        if (this.buffersToWrite.size() > 1) {
          paramAudioBufferInfo = (InstantCameraView.AudioBufferInfo)this.buffersToWrite.get(0);
        }
        try
        {
          drainEncoder(false);
          bool1 = false;
          while (paramAudioBufferInfo != null) {
            try
            {
              j = this.audioEncoder.dequeueInputBuffer(0L);
              if (j < 0) {
                continue;
              }
              if (Build.VERSION.SDK_INT >= 21)
              {
                localByteBuffer = this.audioEncoder.getInputBuffer(j);
                l1 = paramAudioBufferInfo.offset[paramAudioBufferInfo.lastWroteBuffer];
                k = paramAudioBufferInfo.lastWroteBuffer;
                localObject = paramAudioBufferInfo;
                bool2 = bool1;
                paramAudioBufferInfo = (InstantCameraView.AudioBufferInfo)localObject;
                if (k <= ((InstantCameraView.AudioBufferInfo)localObject).results)
                {
                  if (k >= ((InstantCameraView.AudioBufferInfo)localObject).results) {
                    break label754;
                  }
                  if ((this.running) || (localObject.offset[k] < this.videoLast - this.desyncTime)) {
                    break label701;
                  }
                  if (BuildVars.LOGS_ENABLED)
                  {
                    paramAudioBufferInfo = new java/lang/StringBuilder;
                    paramAudioBufferInfo.<init>();
                    FileLog.d("stop audio encoding because of stoped video recording at " + localObject.offset[k] + " last video " + this.videoLast);
                  }
                  this.audioStopedByTime = true;
                  bool2 = true;
                  paramAudioBufferInfo = null;
                  this.buffersToWrite.clear();
                }
                localObject = this.audioEncoder;
                i = localByteBuffer.position();
                if (l1 != 0L) {
                  break label832;
                }
                l1 = 0L;
                if (!bool2) {
                  break label848;
                }
                k = 4;
                ((MediaCodec)localObject).queueInputBuffer(j, 0, i, l1, k);
                bool1 = bool2;
              }
            }
            catch (Throwable paramAudioBufferInfo)
            {
              FileLog.e(paramAudioBufferInfo);
            }
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            boolean bool1;
            long l1;
            boolean bool2;
            FileLog.e(localException);
            continue;
            ByteBuffer localByteBuffer = this.audioEncoder.getInputBuffers()[j];
            localByteBuffer.clear();
            continue;
            label701:
            if (localByteBuffer.remaining() < localException.read[k])
            {
              localException.lastWroteBuffer = k;
              paramAudioBufferInfo = null;
              bool2 = bool1;
            }
            else
            {
              localByteBuffer.put(localException.buffer, k * 2048, localException.read[k]);
              label754:
              paramAudioBufferInfo = localException;
              InstantCameraView.AudioBufferInfo localAudioBufferInfo;
              if (k >= localException.results - 1)
              {
                this.buffersToWrite.remove(localException);
                if (this.running) {
                  this.buffers.put(localException);
                }
                if (!this.buffersToWrite.isEmpty()) {
                  paramAudioBufferInfo = (InstantCameraView.AudioBufferInfo)this.buffersToWrite.get(0);
                }
              }
              else
              {
                k++;
                localAudioBufferInfo = paramAudioBufferInfo;
                continue;
              }
              bool2 = localAudioBufferInfo.last;
              paramAudioBufferInfo = null;
              continue;
              label832:
              long l2 = this.audioStartTime;
              l1 -= l2;
              continue;
              label848:
              k = 0;
            }
          }
        }
      }
    }
    
    private void handleStopRecording(final int paramInt)
    {
      if (this.running)
      {
        this.sendWhenDone = paramInt;
        this.running = false;
      }
      for (;;)
      {
        return;
        try
        {
          drainEncoder(true);
          if (this.videoEncoder == null) {}
        }
        catch (Exception localException3)
        {
          try
          {
            this.videoEncoder.stop();
            this.videoEncoder.release();
            this.videoEncoder = null;
            if (this.audioEncoder == null) {}
          }
          catch (Exception localException3)
          {
            try
            {
              this.audioEncoder.stop();
              this.audioEncoder.release();
              this.audioEncoder = null;
              if (this.mediaMuxer == null) {}
            }
            catch (Exception localException3)
            {
              try
              {
                for (;;)
                {
                  this.mediaMuxer.finishMovie();
                  if (paramInt == 0) {
                    break label255;
                  }
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      InstantCameraView.access$2002(InstantCameraView.this, new VideoEditedInfo());
                      InstantCameraView.this.videoEditedInfo.roundVideo = true;
                      InstantCameraView.this.videoEditedInfo.startTime = -1L;
                      InstantCameraView.this.videoEditedInfo.endTime = -1L;
                      InstantCameraView.this.videoEditedInfo.file = InstantCameraView.this.file;
                      InstantCameraView.this.videoEditedInfo.encryptedFile = InstantCameraView.this.encryptedFile;
                      InstantCameraView.this.videoEditedInfo.key = InstantCameraView.this.key;
                      InstantCameraView.this.videoEditedInfo.iv = InstantCameraView.this.iv;
                      InstantCameraView.this.videoEditedInfo.estimatedSize = InstantCameraView.this.size;
                      InstantCameraView.this.videoEditedInfo.framerate = 25;
                      Object localObject = InstantCameraView.this.videoEditedInfo;
                      InstantCameraView.this.videoEditedInfo.originalWidth = 240;
                      ((VideoEditedInfo)localObject).resultWidth = 240;
                      localObject = InstantCameraView.this.videoEditedInfo;
                      InstantCameraView.this.videoEditedInfo.originalHeight = 240;
                      ((VideoEditedInfo)localObject).resultHeight = 240;
                      InstantCameraView.this.videoEditedInfo.originalPath = InstantCameraView.VideoRecorder.this.videoFile.getAbsolutePath();
                      if (paramInt == 1) {
                        InstantCameraView.this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0L, InstantCameraView.VideoRecorder.this.videoFile.getAbsolutePath(), 0, true), InstantCameraView.this.videoEditedInfo);
                      }
                      for (;;)
                      {
                        InstantCameraView.VideoRecorder.this.didWriteData(InstantCameraView.VideoRecorder.this.videoFile, true);
                        return;
                        InstantCameraView.access$602(InstantCameraView.this, new VideoPlayer());
                        InstantCameraView.this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate()
                        {
                          public void onError(Exception paramAnonymous2Exception)
                          {
                            FileLog.e(paramAnonymous2Exception);
                          }
                          
                          public void onRenderedFirstFrame() {}
                          
                          public void onStateChanged(boolean paramAnonymous2Boolean, int paramAnonymous2Int)
                          {
                            long l = 0L;
                            if (InstantCameraView.this.videoPlayer == null) {}
                            for (;;)
                            {
                              return;
                              if ((InstantCameraView.this.videoPlayer.isPlaying()) && (paramAnonymous2Int == 4))
                              {
                                VideoPlayer localVideoPlayer = InstantCameraView.this.videoPlayer;
                                if (InstantCameraView.this.videoEditedInfo.startTime > 0L) {
                                  l = InstantCameraView.this.videoEditedInfo.startTime;
                                }
                                localVideoPlayer.seekTo(l);
                              }
                            }
                          }
                          
                          public boolean onSurfaceDestroyed(SurfaceTexture paramAnonymous2SurfaceTexture)
                          {
                            return false;
                          }
                          
                          public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymous2SurfaceTexture) {}
                          
                          public void onVideoSizeChanged(int paramAnonymous2Int1, int paramAnonymous2Int2, int paramAnonymous2Int3, float paramAnonymous2Float) {}
                        });
                        InstantCameraView.this.videoPlayer.setTextureView(InstantCameraView.this.textureView);
                        InstantCameraView.this.videoPlayer.preparePlayer(Uri.fromFile(InstantCameraView.VideoRecorder.this.videoFile), "other");
                        InstantCameraView.this.videoPlayer.play();
                        InstantCameraView.this.videoPlayer.setMute(true);
                        InstantCameraView.this.startProgressTimer();
                        localObject = new AnimatorSet();
                        ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, "alpha", new float[] { 0.0F }), ObjectAnimator.ofInt(InstantCameraView.this.paint, "alpha", new int[] { 0 }), ObjectAnimator.ofFloat(InstantCameraView.this.muteImageView, "alpha", new float[] { 1.0F }) });
                        ((AnimatorSet)localObject).setDuration(180L);
                        ((AnimatorSet)localObject).setInterpolator(new DecelerateInterpolator());
                        ((AnimatorSet)localObject).start();
                        InstantCameraView.this.videoEditedInfo.estimatedDuration = InstantCameraView.this.duration;
                        NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.audioDidSent, new Object[] { InstantCameraView.this.videoEditedInfo, InstantCameraView.VideoRecorder.this.videoFile.getAbsolutePath() });
                      }
                    }
                  });
                  EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
                  this.eglSurface = EGL14.EGL_NO_SURFACE;
                  if (this.surface != null)
                  {
                    this.surface.release();
                    this.surface = null;
                  }
                  if (this.eglDisplay != EGL14.EGL_NO_DISPLAY)
                  {
                    EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                    EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
                    EGL14.eglReleaseThread();
                    EGL14.eglTerminate(this.eglDisplay);
                  }
                  this.eglDisplay = EGL14.EGL_NO_DISPLAY;
                  this.eglContext = EGL14.EGL_NO_CONTEXT;
                  this.eglConfig = null;
                  this.handler.exit();
                  break;
                  localException1 = localException1;
                  FileLog.e(localException1);
                  continue;
                  localException2 = localException2;
                  FileLog.e(localException2);
                }
                localException3 = localException3;
                FileLog.e(localException3);
              }
              catch (Exception localException4)
              {
                for (;;)
                {
                  FileLog.e(localException4);
                  continue;
                  label255:
                  FileLoader.getInstance(InstantCameraView.this.currentAccount).cancelUploadFile(this.videoFile.getAbsolutePath(), false);
                  this.videoFile.delete();
                }
              }
            }
          }
        }
      }
    }
    
    private void handleVideoFrameAvailable(long paramLong, Integer paramInteger)
    {
      try
      {
        drainEncoder(false);
        if (!this.lastCameraId.equals(paramInteger))
        {
          this.lastTimestamp = -1L;
          this.lastCameraId = paramInteger;
        }
        if (this.lastTimestamp == -1L)
        {
          this.lastTimestamp = paramLong;
          if (this.currentTimestamp != 0L)
          {
            l1 = (System.currentTimeMillis() - this.lastCommitedFrameTime) * 1000000L;
            l2 = 0L;
            this.lastCommitedFrameTime = System.currentTimeMillis();
            if (this.skippedFirst) {
              break label151;
            }
            this.skippedTime += l1;
            if (this.skippedTime >= 200000000L) {
              break label146;
            }
            return;
          }
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
          continue;
          long l1 = 0L;
          long l2 = 0L;
          continue;
          l1 = paramLong - this.lastTimestamp;
          l2 = l1;
          this.lastTimestamp = paramLong;
          continue;
          label146:
          this.skippedFirst = true;
          label151:
          this.currentTimestamp += l1;
          if (this.videoFirst == -1L)
          {
            this.videoFirst = (paramLong / 1000L);
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("first video frame was at " + this.videoFirst);
            }
          }
          this.videoLast = paramLong;
          GLES20.glUseProgram(this.drawProgram);
          GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, InstantCameraView.this.vertexBuffer);
          GLES20.glEnableVertexAttribArray(this.positionHandle);
          GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, InstantCameraView.this.textureBuffer);
          GLES20.glEnableVertexAttribArray(this.textureHandle);
          GLES20.glUniform1f(this.scaleXHandle, InstantCameraView.this.scaleX);
          GLES20.glUniform1f(this.scaleYHandle, InstantCameraView.this.scaleY);
          GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, InstantCameraView.this.mMVPMatrix, 0);
          GLES20.glActiveTexture(33984);
          if (InstantCameraView.this.oldCameraTexture[0] != 0)
          {
            if (!this.blendEnabled)
            {
              GLES20.glEnable(3042);
              this.blendEnabled = true;
            }
            GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.moldSTMatrix, 0);
            GLES20.glUniform1f(this.alphaHandle, 1.0F);
            GLES20.glBindTexture(36197, InstantCameraView.this.oldCameraTexture[0]);
            GLES20.glDrawArrays(5, 0, 4);
          }
          GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.mSTMatrix, 0);
          GLES20.glUniform1f(this.alphaHandle, InstantCameraView.this.cameraTextureAlpha);
          GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
          GLES20.glDrawArrays(5, 0, 4);
          GLES20.glDisableVertexAttribArray(this.positionHandle);
          GLES20.glDisableVertexAttribArray(this.textureHandle);
          GLES20.glBindTexture(36197, 0);
          GLES20.glUseProgram(0);
          EGLExt.eglPresentationTimeANDROID(this.eglDisplay, this.eglSurface, this.currentTimestamp);
          EGL14.eglSwapBuffers(this.eglDisplay, this.eglSurface);
          if ((InstantCameraView.this.oldCameraTexture[0] != 0) && (InstantCameraView.this.cameraTextureAlpha < 1.0F))
          {
            InstantCameraView.access$3402(InstantCameraView.this, InstantCameraView.this.cameraTextureAlpha + (float)l2 / 2.0E8F);
            if (InstantCameraView.this.cameraTextureAlpha > 1.0F)
            {
              GLES20.glDisable(3042);
              this.blendEnabled = false;
              InstantCameraView.access$3402(InstantCameraView.this, 1.0F);
              GLES20.glDeleteTextures(1, InstantCameraView.this.oldCameraTexture, 0);
              InstantCameraView.this.oldCameraTexture[0] = 0;
              if (!InstantCameraView.this.cameraReady) {
                InstantCameraView.access$902(InstantCameraView.this, true);
              }
            }
          }
          else if (!InstantCameraView.this.cameraReady)
          {
            InstantCameraView.access$902(InstantCameraView.this, true);
          }
        }
      }
    }
    
    private void prepareEncoder()
    {
      Object localObject3;
      try
      {
        i = AudioRecord.getMinBufferSize(44100, 16, 2);
        j = i;
        if (i <= 0) {
          j = 3584;
        }
        i = 49152;
        if (49152 < j) {
          i = (j / 2048 + 1) * 2048 * 2;
        }
        for (j = 0; j < 3; j++)
        {
          localObject1 = this.buffers;
          localObject3 = new org/telegram/ui/Components/InstantCameraView$AudioBufferInfo;
          ((InstantCameraView.AudioBufferInfo)localObject3).<init>(InstantCameraView.this, null);
          ((ArrayBlockingQueue)localObject1).add(localObject3);
        }
        Object localObject1 = new android/media/AudioRecord;
        ((AudioRecord)localObject1).<init>(1, 44100, 16, 2, i);
        this.audioRecorder = ((AudioRecord)localObject1);
        this.audioRecorder.startRecording();
        if (BuildVars.LOGS_ENABLED)
        {
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          FileLog.d("initied audio record with channels " + this.audioRecorder.getChannelCount() + " sample rate = " + this.audioRecorder.getSampleRate() + " bufferSize = " + i);
        }
        localObject1 = new java/lang/Thread;
        ((Thread)localObject1).<init>(this.recorderRunnable);
        ((Thread)localObject1).setPriority(10);
        ((Thread)localObject1).start();
        localObject1 = new android/media/MediaCodec$BufferInfo;
        ((MediaCodec.BufferInfo)localObject1).<init>();
        this.audioBufferInfo = ((MediaCodec.BufferInfo)localObject1);
        localObject1 = new android/media/MediaCodec$BufferInfo;
        ((MediaCodec.BufferInfo)localObject1).<init>();
        this.videoBufferInfo = ((MediaCodec.BufferInfo)localObject1);
        localObject1 = new android/media/MediaFormat;
        ((MediaFormat)localObject1).<init>();
        ((MediaFormat)localObject1).setString("mime", "audio/mp4a-latm");
        ((MediaFormat)localObject1).setInteger("aac-profile", 2);
        ((MediaFormat)localObject1).setInteger("sample-rate", 44100);
        ((MediaFormat)localObject1).setInteger("channel-count", 1);
        ((MediaFormat)localObject1).setInteger("bitrate", 32000);
        ((MediaFormat)localObject1).setInteger("max-input-size", 20480);
        this.audioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
        this.audioEncoder.configure((MediaFormat)localObject1, null, null, 1);
        this.audioEncoder.start();
        this.videoEncoder = MediaCodec.createEncoderByType("video/avc");
        localObject1 = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
        ((MediaFormat)localObject1).setInteger("color-format", NUM);
        ((MediaFormat)localObject1).setInteger("bitrate", this.videoBitrate);
        ((MediaFormat)localObject1).setInteger("frame-rate", 30);
        ((MediaFormat)localObject1).setInteger("i-frame-interval", 1);
        this.videoEncoder.configure((MediaFormat)localObject1, null, null, 1);
        this.surface = this.videoEncoder.createInputSurface();
        this.videoEncoder.start();
        localObject1 = new org/telegram/messenger/video/Mp4Movie;
        ((Mp4Movie)localObject1).<init>();
        ((Mp4Movie)localObject1).setCacheFile(this.videoFile);
        ((Mp4Movie)localObject1).setRotation(0);
        ((Mp4Movie)localObject1).setSize(this.videoWidth, this.videoHeight);
        localObject3 = new org/telegram/messenger/video/MP4Builder;
        ((MP4Builder)localObject3).<init>();
        this.mediaMuxer = ((MP4Builder)localObject3).createMovie((Mp4Movie)localObject1, InstantCameraView.this.isSecretChat);
        localObject1 = new org/telegram/ui/Components/InstantCameraView$VideoRecorder$3;
        ((3)localObject1).<init>(this);
        AndroidUtilities.runOnUIThread((Runnable)localObject1);
        if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
          throw new RuntimeException("EGL already set up");
        }
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException);
      }
      this.eglDisplay = EGL14.eglGetDisplay(0);
      if (this.eglDisplay == EGL14.EGL_NO_DISPLAY) {
        throw new RuntimeException("unable to get EGL14 display");
      }
      Object localObject2 = new int[2];
      if (!EGL14.eglInitialize(this.eglDisplay, (int[])localObject2, 0, (int[])localObject2, 1))
      {
        this.eglDisplay = null;
        throw new RuntimeException("unable to initialize EGL14");
      }
      if (this.eglContext == EGL14.EGL_NO_CONTEXT)
      {
        android.opengl.EGLConfig[] arrayOfEGLConfig = new android.opengl.EGLConfig[1];
        localObject3 = new int[1];
        localObject2 = this.eglDisplay;
        i = arrayOfEGLConfig.length;
        if (!EGL14.eglChooseConfig((android.opengl.EGLDisplay)localObject2, new int[] { 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12610, 1, 12344 }, 0, arrayOfEGLConfig, 0, i, (int[])localObject3, 0)) {
          throw new RuntimeException("Unable to find a suitable EGLConfig");
        }
        this.eglContext = EGL14.eglCreateContext(this.eglDisplay, arrayOfEGLConfig[0], this.sharedEglContext, new int[] { 12440, 2, 12344 }, 0);
        this.eglConfig = arrayOfEGLConfig[0];
      }
      localObject2 = new int[1];
      EGL14.eglQueryContext(this.eglDisplay, this.eglContext, 12440, (int[])localObject2, 0);
      if (this.eglSurface != EGL14.EGL_NO_SURFACE) {
        throw new IllegalStateException("surface already created");
      }
      this.eglSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surface, new int[] { 12344 }, 0);
      if (this.eglSurface == null) {
        throw new RuntimeException("surface was null");
      }
      if (!EGL14.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext))
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
        }
        throw new RuntimeException("eglMakeCurrent failed");
      }
      GLES20.glBlendFunc(770, 771);
      int i = InstantCameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
      int j = InstantCameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float scaleX;\nuniform float scaleY;\nuniform float alpha;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec2 coord = vec2((vTextureCoord.x - 0.5) * scaleX, (vTextureCoord.y - 0.5) * scaleY);\n   float coef = ceil(clamp(0.2601 - dot(coord, coord), 0.0, 1.0));\n   vec3 color = texture2D(sTexture, vTextureCoord).rgb * coef + (1.0 - step(0.001, coef));\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n");
      if ((i != 0) && (j != 0))
      {
        this.drawProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(this.drawProgram, i);
        GLES20.glAttachShader(this.drawProgram, j);
        GLES20.glLinkProgram(this.drawProgram);
        localObject2 = new int[1];
        GLES20.glGetProgramiv(this.drawProgram, 35714, (int[])localObject2, 0);
        if (localObject2[0] != 0) {
          break label1021;
        }
        GLES20.glDeleteProgram(this.drawProgram);
        this.drawProgram = 0;
      }
      for (;;)
      {
        return;
        label1021:
        this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
        this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
        this.scaleXHandle = GLES20.glGetUniformLocation(this.drawProgram, "scaleX");
        this.scaleYHandle = GLES20.glGetUniformLocation(this.drawProgram, "scaleY");
        this.alphaHandle = GLES20.glGetUniformLocation(this.drawProgram, "alpha");
        this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
        this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
      }
    }
    
    public void drainEncoder(boolean paramBoolean)
      throws Exception
    {
      if (paramBoolean) {
        this.videoEncoder.signalEndOfInputStream();
      }
      ByteBuffer[] arrayOfByteBuffer = null;
      if (Build.VERSION.SDK_INT < 21) {
        arrayOfByteBuffer = this.videoEncoder.getOutputBuffers();
      }
      int i;
      do
      {
        i = this.videoEncoder.dequeueOutputBuffer(this.videoBufferInfo, 10000L);
        if (i != -1) {
          break;
        }
      } while (paramBoolean);
      label53:
      if (Build.VERSION.SDK_INT < 21) {
        arrayOfByteBuffer = this.audioEncoder.getOutputBuffers();
      }
      label69:
      int j;
      do
      {
        j = this.audioEncoder.dequeueOutputBuffer(this.audioBufferInfo, 0L);
        if (j != -1) {
          break;
        }
      } while ((paramBoolean) && ((this.running) || (this.sendWhenDone != 0)));
      for (;;)
      {
        return;
        if (i == -3)
        {
          if (Build.VERSION.SDK_INT >= 21) {
            break;
          }
          arrayOfByteBuffer = this.videoEncoder.getOutputBuffers();
          break;
        }
        if (i == -2)
        {
          localObject1 = this.videoEncoder.getOutputFormat();
          if (this.videoTrackIndex != -5) {
            break;
          }
          this.videoTrackIndex = this.mediaMuxer.addTrack((MediaFormat)localObject1, false);
          break;
        }
        if (i < 0) {
          break;
        }
        if (Build.VERSION.SDK_INT < 21) {}
        for (Object localObject1 = arrayOfByteBuffer[i]; localObject1 == null; localObject1 = this.videoEncoder.getOutputBuffer(i)) {
          throw new RuntimeException("encoderOutputBuffer " + i + " was null");
        }
        if (this.videoBufferInfo.size > 1)
        {
          if ((this.videoBufferInfo.flags & 0x2) != 0) {
            break label320;
          }
          if (this.mediaMuxer.writeSampleData(this.videoTrackIndex, (ByteBuffer)localObject1, this.videoBufferInfo, true)) {
            didWriteData(this.videoFile, false);
          }
        }
        label320:
        while (this.videoTrackIndex != -5)
        {
          this.videoEncoder.releaseOutputBuffer(i, false);
          if ((this.videoBufferInfo.flags & 0x4) == 0) {
            break;
          }
          break label53;
        }
        byte[] arrayOfByte = new byte[this.videoBufferInfo.size];
        ((ByteBuffer)localObject1).limit(this.videoBufferInfo.offset + this.videoBufferInfo.size);
        ((ByteBuffer)localObject1).position(this.videoBufferInfo.offset);
        ((ByteBuffer)localObject1).get(arrayOfByte);
        MediaFormat localMediaFormat = null;
        Object localObject2 = null;
        for (j = this.videoBufferInfo.size - 1;; j--)
        {
          Object localObject3 = localObject2;
          localObject1 = localMediaFormat;
          if (j >= 0)
          {
            localObject3 = localObject2;
            localObject1 = localMediaFormat;
            if (j > 3)
            {
              if ((arrayOfByte[j] != 1) || (arrayOfByte[(j - 1)] != 0) || (arrayOfByte[(j - 2)] != 0) || (arrayOfByte[(j - 3)] != 0)) {
                continue;
              }
              localObject1 = ByteBuffer.allocate(j - 3);
              localObject3 = ByteBuffer.allocate(this.videoBufferInfo.size - (j - 3));
              ((ByteBuffer)localObject1).put(arrayOfByte, 0, j - 3).position(0);
              ((ByteBuffer)localObject3).put(arrayOfByte, j - 3, this.videoBufferInfo.size - (j - 3)).position(0);
            }
          }
          localMediaFormat = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
          if ((localObject1 != null) && (localObject3 != null))
          {
            localMediaFormat.setByteBuffer("csd-0", (ByteBuffer)localObject1);
            localMediaFormat.setByteBuffer("csd-1", (ByteBuffer)localObject3);
          }
          this.videoTrackIndex = this.mediaMuxer.addTrack(localMediaFormat, false);
          break;
        }
        if (j == -3)
        {
          if (Build.VERSION.SDK_INT >= 21) {
            break label69;
          }
          arrayOfByteBuffer = this.audioEncoder.getOutputBuffers();
          break label69;
        }
        if (j == -2)
        {
          localObject1 = this.audioEncoder.getOutputFormat();
          if (this.audioTrackIndex != -5) {
            break label69;
          }
          this.audioTrackIndex = this.mediaMuxer.addTrack((MediaFormat)localObject1, true);
          break label69;
        }
        if (j < 0) {
          break label69;
        }
        if (Build.VERSION.SDK_INT < 21) {}
        for (localObject1 = arrayOfByteBuffer[j]; localObject1 == null; localObject1 = this.audioEncoder.getOutputBuffer(j)) {
          throw new RuntimeException("encoderOutputBuffer " + j + " was null");
        }
        if ((this.audioBufferInfo.flags & 0x2) != 0) {
          this.audioBufferInfo.size = 0;
        }
        if ((this.audioBufferInfo.size != 0) && (this.mediaMuxer.writeSampleData(this.audioTrackIndex, (ByteBuffer)localObject1, this.audioBufferInfo, false))) {
          didWriteData(this.videoFile, false);
        }
        this.audioEncoder.releaseOutputBuffer(j, false);
        if ((this.audioBufferInfo.flags & 0x4) == 0) {
          break label69;
        }
      }
    }
    
    protected void finalize()
      throws Throwable
    {
      try
      {
        if (this.eglDisplay != EGL14.EGL_NO_DISPLAY)
        {
          EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
          EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
          EGL14.eglReleaseThread();
          EGL14.eglTerminate(this.eglDisplay);
          this.eglDisplay = EGL14.EGL_NO_DISPLAY;
          this.eglContext = EGL14.EGL_NO_CONTEXT;
          this.eglConfig = null;
        }
        return;
      }
      finally
      {
        super.finalize();
      }
    }
    
    public void frameAvailable(SurfaceTexture paramSurfaceTexture, Integer paramInteger, long paramLong)
    {
      for (;;)
      {
        long l;
        synchronized (this.sync)
        {
          if (!this.ready) {
            return;
          }
          l = paramSurfaceTexture.getTimestamp();
          if (l == 0L)
          {
            this.zeroTimeStamps += 1;
            if (this.zeroTimeStamps <= 1) {
              continue;
            }
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("fix timestamp enabled");
            }
            this.handler.sendMessage(this.handler.obtainMessage(2, (int)(paramLong >> 32), (int)paramLong, paramInteger));
          }
        }
        this.zeroTimeStamps = 0;
        paramLong = l;
      }
    }
    
    public Surface getInputSurface()
    {
      return this.surface;
    }
    
    public void run()
    {
      
      synchronized (this.sync)
      {
        InstantCameraView.EncoderHandler localEncoderHandler = new org/telegram/ui/Components/InstantCameraView$EncoderHandler;
        localEncoderHandler.<init>(this);
        this.handler = localEncoderHandler;
        this.ready = true;
        this.sync.notify();
        Looper.loop();
      }
      synchronized (this.sync)
      {
        this.ready = false;
        return;
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
    }
    
    public void startRecording(File arg1, android.opengl.EGLContext paramEGLContext)
    {
      String str1 = Build.DEVICE;
      String str2 = str1;
      if (str1 == null) {
        str2 = "";
      }
      int i;
      int j;
      if ((str2.startsWith("zeroflte")) || (str2.startsWith("zenlte")))
      {
        i = 320;
        j = 600000;
      }
      for (;;)
      {
        this.videoFile = ???;
        this.videoWidth = i;
        this.videoHeight = i;
        this.videoBitrate = j;
        this.sharedEglContext = paramEGLContext;
        synchronized (this.sync)
        {
          if (this.running)
          {
            return;
            i = 240;
            j = 400000;
            continue;
          }
          this.running = true;
          paramEGLContext = new java/lang/Thread;
          paramEGLContext.<init>(this, "TextureMovieEncoder");
          paramEGLContext.setPriority(10);
          paramEGLContext.start();
          for (;;)
          {
            boolean bool = this.ready;
            if (bool) {
              break;
            }
            try
            {
              this.sync.wait();
            }
            catch (InterruptedException paramEGLContext) {}
          }
          this.handler.sendMessage(this.handler.obtainMessage(0));
        }
      }
    }
    
    public void stopRecording(int paramInt)
    {
      this.handler.sendMessage(this.handler.obtainMessage(1, paramInt, 0));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/InstantCameraView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */