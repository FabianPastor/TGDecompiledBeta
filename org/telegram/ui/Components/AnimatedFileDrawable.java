package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class AnimatedFileDrawable
  extends BitmapDrawable
  implements Animatable
{
  private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, new ThreadPoolExecutor.DiscardPolicy());
  private static final Handler uiHandler = new Handler(Looper.getMainLooper());
  private RectF actualDrawRect = new RectF();
  private boolean applyTransformation;
  private Bitmap backgroundBitmap;
  private BitmapShader backgroundShader;
  private RectF bitmapRect = new RectF();
  private boolean decodeSingleFrame;
  private boolean decoderCreated;
  private boolean destroyWhenDone;
  private final Rect dstRect = new Rect();
  private int invalidateAfter = 50;
  private volatile boolean isRecycled;
  private volatile boolean isRunning;
  private long lastFrameDecodeTime;
  private long lastFrameTime;
  private int lastTimeStamp;
  private Runnable loadFrameRunnable = new Runnable()
  {
    public void run()
    {
      if (!AnimatedFileDrawable.this.isRecycled) {
        if ((!AnimatedFileDrawable.this.decoderCreated) && (AnimatedFileDrawable.this.nativePtr == 0L))
        {
          AnimatedFileDrawable.access$302(AnimatedFileDrawable.this, AnimatedFileDrawable.createDecoder(AnimatedFileDrawable.this.path.getAbsolutePath(), AnimatedFileDrawable.this.metaData));
          AnimatedFileDrawable.access$1702(AnimatedFileDrawable.this, true);
        }
      }
      for (;;)
      {
        try
        {
          localObject = AnimatedFileDrawable.this.backgroundBitmap;
          if (localObject != null) {}
        }
        catch (Throwable localThrowable2)
        {
          Object localObject;
          FileLog.e(localThrowable2);
          continue;
        }
        try
        {
          AnimatedFileDrawable.access$602(AnimatedFileDrawable.this, Bitmap.createBitmap(AnimatedFileDrawable.this.metaData[0], AnimatedFileDrawable.this.metaData[1], Bitmap.Config.ARGB_8888));
          if ((AnimatedFileDrawable.this.backgroundShader == null) && (AnimatedFileDrawable.this.backgroundBitmap != null) && (AnimatedFileDrawable.this.roundRadius != 0))
          {
            localObject = AnimatedFileDrawable.this;
            BitmapShader localBitmapShader = new android/graphics/BitmapShader;
            localBitmapShader.<init>(AnimatedFileDrawable.this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            AnimatedFileDrawable.access$1102((AnimatedFileDrawable)localObject, localBitmapShader);
          }
          if (AnimatedFileDrawable.this.backgroundBitmap != null)
          {
            AnimatedFileDrawable.access$2102(AnimatedFileDrawable.this, System.currentTimeMillis());
            AnimatedFileDrawable.getVideoFrame(AnimatedFileDrawable.this.nativePtr, AnimatedFileDrawable.this.backgroundBitmap, AnimatedFileDrawable.this.metaData);
          }
          AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnable);
          return;
        }
        catch (Throwable localThrowable1)
        {
          FileLog.e(localThrowable1);
        }
      }
    }
  };
  private Runnable loadFrameTask;
  protected final Runnable mInvalidateTask = new Runnable()
  {
    public void run()
    {
      if (AnimatedFileDrawable.this.secondParentView != null) {
        AnimatedFileDrawable.this.secondParentView.invalidate();
      }
      for (;;)
      {
        return;
        if (AnimatedFileDrawable.this.parentView != null) {
          AnimatedFileDrawable.this.parentView.invalidate();
        }
      }
    }
  };
  private final Runnable mStartTask = new Runnable()
  {
    public void run()
    {
      if (AnimatedFileDrawable.this.secondParentView != null) {
        AnimatedFileDrawable.this.secondParentView.invalidate();
      }
      for (;;)
      {
        return;
        if (AnimatedFileDrawable.this.parentView != null) {
          AnimatedFileDrawable.this.parentView.invalidate();
        }
      }
    }
  };
  private final int[] metaData = new int[4];
  private volatile long nativePtr;
  private Bitmap nextRenderingBitmap;
  private BitmapShader nextRenderingShader;
  private View parentView = null;
  private File path;
  private boolean recycleWithSecond;
  private Bitmap renderingBitmap;
  private BitmapShader renderingShader;
  private int roundRadius;
  private RectF roundRect = new RectF();
  private float scaleX = 1.0F;
  private float scaleY = 1.0F;
  private View secondParentView = null;
  private Matrix shaderMatrix = new Matrix();
  private boolean singleFrameDecoded;
  private Runnable uiRunnable = new Runnable()
  {
    public void run()
    {
      if ((AnimatedFileDrawable.this.destroyWhenDone) && (AnimatedFileDrawable.this.nativePtr != 0L))
      {
        AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
        AnimatedFileDrawable.access$302(AnimatedFileDrawable.this, 0L);
      }
      if (AnimatedFileDrawable.this.nativePtr == 0L)
      {
        if (AnimatedFileDrawable.this.renderingBitmap != null)
        {
          AnimatedFileDrawable.this.renderingBitmap.recycle();
          AnimatedFileDrawable.access$502(AnimatedFileDrawable.this, null);
        }
        if (AnimatedFileDrawable.this.backgroundBitmap != null)
        {
          AnimatedFileDrawable.this.backgroundBitmap.recycle();
          AnimatedFileDrawable.access$602(AnimatedFileDrawable.this, null);
        }
        return;
      }
      AnimatedFileDrawable.access$702(AnimatedFileDrawable.this, true);
      AnimatedFileDrawable.access$802(AnimatedFileDrawable.this, null);
      AnimatedFileDrawable.access$902(AnimatedFileDrawable.this, AnimatedFileDrawable.this.backgroundBitmap);
      AnimatedFileDrawable.access$1002(AnimatedFileDrawable.this, AnimatedFileDrawable.this.backgroundShader);
      if (AnimatedFileDrawable.this.metaData[3] < AnimatedFileDrawable.this.lastTimeStamp) {
        AnimatedFileDrawable.access$1302(AnimatedFileDrawable.this, 0);
      }
      if (AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp != 0) {
        AnimatedFileDrawable.access$1402(AnimatedFileDrawable.this, AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp);
      }
      AnimatedFileDrawable.access$1302(AnimatedFileDrawable.this, AnimatedFileDrawable.this.metaData[3]);
      if (AnimatedFileDrawable.this.secondParentView != null) {
        AnimatedFileDrawable.this.secondParentView.invalidate();
      }
      for (;;)
      {
        AnimatedFileDrawable.this.scheduleNextGetFrame();
        break;
        if (AnimatedFileDrawable.this.parentView != null) {
          AnimatedFileDrawable.this.parentView.invalidate();
        }
      }
    }
  };
  
  public AnimatedFileDrawable(File paramFile, boolean paramBoolean)
  {
    this.path = paramFile;
    if (paramBoolean)
    {
      this.nativePtr = createDecoder(paramFile.getAbsolutePath(), this.metaData);
      this.decoderCreated = true;
    }
  }
  
  private static native long createDecoder(String paramString, int[] paramArrayOfInt);
  
  private static native void destroyDecoder(long paramLong);
  
  private static native int getVideoFrame(long paramLong, Bitmap paramBitmap, int[] paramArrayOfInt);
  
  protected static void runOnUiThread(Runnable paramRunnable)
  {
    if (Looper.myLooper() == uiHandler.getLooper()) {
      paramRunnable.run();
    }
    for (;;)
    {
      return;
      uiHandler.post(paramRunnable);
    }
  }
  
  private void scheduleNextGetFrame()
  {
    if ((this.loadFrameTask != null) || ((this.nativePtr == 0L) && (this.decoderCreated)) || (this.destroyWhenDone) || ((!this.isRunning) && ((!this.decodeSingleFrame) || ((this.decodeSingleFrame) && (this.singleFrameDecoded))))) {}
    for (;;)
    {
      return;
      long l = 0L;
      if (this.lastFrameDecodeTime != 0L) {
        l = Math.min(this.invalidateAfter, Math.max(0L, this.invalidateAfter - (System.currentTimeMillis() - this.lastFrameDecodeTime)));
      }
      ScheduledThreadPoolExecutor localScheduledThreadPoolExecutor = executor;
      Runnable localRunnable = this.loadFrameRunnable;
      this.loadFrameTask = localRunnable;
      localScheduledThreadPoolExecutor.schedule(localRunnable, l, TimeUnit.MILLISECONDS);
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (((this.nativePtr == 0L) && (this.decoderCreated)) || (this.destroyWhenDone)) {
      return;
    }
    long l = System.currentTimeMillis();
    label53:
    int i;
    int m;
    float f;
    if (this.isRunning) {
      if ((this.renderingBitmap == null) && (this.nextRenderingBitmap == null))
      {
        scheduleNextGetFrame();
        if (this.renderingBitmap == null) {
          break label615;
        }
        if (this.applyTransformation)
        {
          i = this.renderingBitmap.getWidth();
          int j = this.renderingBitmap.getHeight();
          int k;
          if (this.metaData[2] != 90)
          {
            k = j;
            m = i;
            if (this.metaData[2] != 270) {}
          }
          else
          {
            m = j;
            k = i;
          }
          this.dstRect.set(getBounds());
          this.scaleX = (this.dstRect.width() / m);
          this.scaleY = (this.dstRect.height() / k);
          this.applyTransformation = false;
        }
        if (this.roundRadius == 0) {
          break label708;
        }
        f = Math.max(this.scaleX, this.scaleY);
        if (this.renderingShader == null) {
          this.renderingShader = new BitmapShader(this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
        getPaint().setShader(this.renderingShader);
        this.roundRect.set(this.dstRect);
        this.shaderMatrix.reset();
        if (Math.abs(this.scaleX - this.scaleY) <= 1.0E-5F) {
          break label656;
        }
        if ((this.metaData[2] != 90) && (this.metaData[2] != 270)) {
          break label617;
        }
        m = (int)Math.floor(this.dstRect.height() / f);
        i = (int)Math.floor(this.dstRect.width() / f);
        label328:
        this.bitmapRect.set((this.renderingBitmap.getWidth() - m) / 2, (this.renderingBitmap.getHeight() - i) / 2, m, i);
        AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.metaData[2], Matrix.ScaleToFit.START);
      }
    }
    for (;;)
    {
      this.renderingShader.setLocalMatrix(this.shaderMatrix);
      paramCanvas.drawRoundRect(this.actualDrawRect, this.roundRadius, this.roundRadius, getPaint());
      label424:
      if (!this.isRunning) {
        break label787;
      }
      l = Math.max(1L, this.invalidateAfter - (l - this.lastFrameTime) - 17L);
      uiHandler.removeCallbacks(this.mInvalidateTask);
      uiHandler.postDelayed(this.mInvalidateTask, Math.min(l, this.invalidateAfter));
      break;
      if ((Math.abs(l - this.lastFrameTime) < this.invalidateAfter) || (this.nextRenderingBitmap == null)) {
        break label53;
      }
      this.renderingBitmap = this.nextRenderingBitmap;
      this.renderingShader = this.nextRenderingShader;
      this.nextRenderingBitmap = null;
      this.nextRenderingShader = null;
      this.lastFrameTime = l;
      break label53;
      if ((this.isRunning) || (!this.decodeSingleFrame) || (Math.abs(l - this.lastFrameTime) < this.invalidateAfter) || (this.nextRenderingBitmap == null)) {
        break label53;
      }
      this.renderingBitmap = this.nextRenderingBitmap;
      this.renderingShader = this.nextRenderingShader;
      this.nextRenderingBitmap = null;
      this.nextRenderingShader = null;
      this.lastFrameTime = l;
      break label53;
      label615:
      break;
      label617:
      m = (int)Math.floor(this.dstRect.width() / f);
      i = (int)Math.floor(this.dstRect.height() / f);
      break label328;
      label656:
      this.bitmapRect.set(0.0F, 0.0F, this.renderingBitmap.getWidth(), this.renderingBitmap.getHeight());
      AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.metaData[2], Matrix.ScaleToFit.FILL);
    }
    label708:
    paramCanvas.translate(this.dstRect.left, this.dstRect.top);
    if (this.metaData[2] == 90)
    {
      paramCanvas.rotate(90.0F);
      paramCanvas.translate(0.0F, -this.dstRect.width());
    }
    for (;;)
    {
      paramCanvas.scale(this.scaleX, this.scaleY);
      paramCanvas.drawBitmap(this.renderingBitmap, 0.0F, 0.0F, getPaint());
      break label424;
      label787:
      break;
      if (this.metaData[2] == 180)
      {
        paramCanvas.rotate(180.0F);
        paramCanvas.translate(-this.dstRect.width(), -this.dstRect.height());
      }
      else if (this.metaData[2] == 270)
      {
        paramCanvas.rotate(270.0F);
        paramCanvas.translate(-this.dstRect.height(), 0.0F);
      }
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      recycle();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public Bitmap getAnimatedBitmap()
  {
    Bitmap localBitmap;
    if (this.renderingBitmap != null) {
      localBitmap = this.renderingBitmap;
    }
    for (;;)
    {
      return localBitmap;
      if (this.nextRenderingBitmap != null) {
        localBitmap = this.nextRenderingBitmap;
      } else {
        localBitmap = null;
      }
    }
  }
  
  public int getIntrinsicHeight()
  {
    int i;
    if (this.decoderCreated) {
      if ((this.metaData[2] == 90) || (this.metaData[2] == 270)) {
        i = this.metaData[0];
      }
    }
    for (;;)
    {
      return i;
      i = this.metaData[1];
      continue;
      i = AndroidUtilities.dp(100.0F);
    }
  }
  
  public int getIntrinsicWidth()
  {
    int i;
    if (this.decoderCreated) {
      if ((this.metaData[2] == 90) || (this.metaData[2] == 270)) {
        i = this.metaData[1];
      }
    }
    for (;;)
    {
      return i;
      i = this.metaData[0];
      continue;
      i = AndroidUtilities.dp(100.0F);
    }
  }
  
  public int getMinimumHeight()
  {
    int i;
    if (this.decoderCreated) {
      if ((this.metaData[2] == 90) || (this.metaData[2] == 270)) {
        i = this.metaData[0];
      }
    }
    for (;;)
    {
      return i;
      i = this.metaData[1];
      continue;
      i = AndroidUtilities.dp(100.0F);
    }
  }
  
  public int getMinimumWidth()
  {
    int i;
    if (this.decoderCreated) {
      if ((this.metaData[2] == 90) || (this.metaData[2] == 270)) {
        i = this.metaData[1];
      }
    }
    for (;;)
    {
      return i;
      i = this.metaData[0];
      continue;
      i = AndroidUtilities.dp(100.0F);
    }
  }
  
  public int getOpacity()
  {
    return -2;
  }
  
  public int getOrientation()
  {
    return this.metaData[2];
  }
  
  public boolean hasBitmap()
  {
    if ((this.nativePtr != 0L) && ((this.renderingBitmap != null) || (this.nextRenderingBitmap != null))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isRunning()
  {
    return this.isRunning;
  }
  
  public AnimatedFileDrawable makeCopy()
  {
    AnimatedFileDrawable localAnimatedFileDrawable = new AnimatedFileDrawable(this.path, false);
    localAnimatedFileDrawable.metaData[0] = this.metaData[0];
    localAnimatedFileDrawable.metaData[1] = this.metaData[1];
    return localAnimatedFileDrawable;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    super.onBoundsChange(paramRect);
    this.applyTransformation = true;
  }
  
  public void recycle()
  {
    if (this.secondParentView != null) {
      this.recycleWithSecond = true;
    }
    for (;;)
    {
      return;
      this.isRunning = false;
      this.isRecycled = true;
      if (this.loadFrameTask == null)
      {
        if (this.nativePtr != 0L)
        {
          destroyDecoder(this.nativePtr);
          this.nativePtr = 0L;
        }
        if (this.renderingBitmap != null)
        {
          this.renderingBitmap.recycle();
          this.renderingBitmap = null;
        }
        if (this.nextRenderingBitmap != null)
        {
          this.nextRenderingBitmap.recycle();
          this.nextRenderingBitmap = null;
        }
      }
      else
      {
        this.destroyWhenDone = true;
      }
    }
  }
  
  public void setActualDrawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.actualDrawRect.set(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
  }
  
  public void setAllowDecodeSingleFrame(boolean paramBoolean)
  {
    this.decodeSingleFrame = paramBoolean;
    if (this.decodeSingleFrame) {
      scheduleNextGetFrame();
    }
  }
  
  public void setParentView(View paramView)
  {
    this.parentView = paramView;
  }
  
  public void setRoundRadius(int paramInt)
  {
    this.roundRadius = paramInt;
    getPaint().setFlags(1);
  }
  
  public void setSecondParentView(View paramView)
  {
    this.secondParentView = paramView;
    if ((paramView == null) && (this.recycleWithSecond)) {
      recycle();
    }
  }
  
  public void start()
  {
    if (this.isRunning) {}
    for (;;)
    {
      return;
      this.isRunning = true;
      scheduleNextGetFrame();
      runOnUiThread(this.mStartTask);
    }
  }
  
  public void stop()
  {
    this.isRunning = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/AnimatedFileDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */