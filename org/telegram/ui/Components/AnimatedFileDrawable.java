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
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class AnimatedFileDrawable
  extends BitmapDrawable
  implements Animatable
{
  private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, new ThreadPoolExecutor.DiscardPolicy());
  private static final Handler uiHandler = new Handler(Looper.getMainLooper());
  private boolean applyTransformation;
  private Bitmap backgroundBitmap;
  private BitmapShader backgroundShader;
  private RectF bitmapRect = new RectF();
  private boolean decoderCreated;
  private boolean destroyWhenDone;
  private final Rect dstRect = new Rect();
  private int invalidateAfter = 50;
  private volatile boolean isRecycled;
  private volatile boolean isRunning;
  private long lastFrameTime;
  private int lastTimeStamp;
  private Runnable loadFrameRunnable = new Runnable()
  {
    public void run()
    {
      if (!AnimatedFileDrawable.this.isRecycled) {
        if ((!AnimatedFileDrawable.this.decoderCreated) && (AnimatedFileDrawable.this.nativePtr == 0))
        {
          AnimatedFileDrawable.access$302(AnimatedFileDrawable.this, AnimatedFileDrawable.createDecoder(AnimatedFileDrawable.this.path.getAbsolutePath(), AnimatedFileDrawable.this.metaData));
          AnimatedFileDrawable.access$1402(AnimatedFileDrawable.this, true);
        }
      }
      for (;;)
      {
        try
        {
          Bitmap localBitmap = AnimatedFileDrawable.this.backgroundBitmap;
          if (localBitmap != null) {}
        }
        catch (Throwable localThrowable2)
        {
          FileLog.e("tmessages", localThrowable2);
          continue;
        }
        try
        {
          AnimatedFileDrawable.access$502(AnimatedFileDrawable.this, Bitmap.createBitmap(AnimatedFileDrawable.this.metaData[0], AnimatedFileDrawable.this.metaData[1], Bitmap.Config.ARGB_8888));
          if ((AnimatedFileDrawable.this.backgroundShader == null) && (AnimatedFileDrawable.this.backgroundBitmap != null) && (AnimatedFileDrawable.this.roundRadius != 0)) {
            AnimatedFileDrawable.access$902(AnimatedFileDrawable.this, new BitmapShader(AnimatedFileDrawable.this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
          }
          if (AnimatedFileDrawable.this.backgroundBitmap != null) {
            AnimatedFileDrawable.getVideoFrame(AnimatedFileDrawable.this.nativePtr, AnimatedFileDrawable.this.backgroundBitmap, AnimatedFileDrawable.this.metaData);
          }
          AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnable);
          return;
        }
        catch (Throwable localThrowable1)
        {
          FileLog.e("tmessages", localThrowable1);
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
      while (AnimatedFileDrawable.this.parentView == null) {
        return;
      }
      AnimatedFileDrawable.this.parentView.invalidate();
    }
  };
  private final Runnable mStartTask = new Runnable()
  {
    public void run()
    {
      if (AnimatedFileDrawable.this.secondParentView != null) {
        AnimatedFileDrawable.this.secondParentView.invalidate();
      }
      while (AnimatedFileDrawable.this.parentView == null) {
        return;
      }
      AnimatedFileDrawable.this.parentView.invalidate();
    }
  };
  private final int[] metaData = new int[4];
  private volatile int nativePtr;
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
  private Runnable uiRunnable = new Runnable()
  {
    public void run()
    {
      if ((AnimatedFileDrawable.this.destroyWhenDone) && (AnimatedFileDrawable.this.nativePtr != 0))
      {
        AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
        AnimatedFileDrawable.access$302(AnimatedFileDrawable.this, 0);
      }
      if (AnimatedFileDrawable.this.nativePtr == 0) {
        if (AnimatedFileDrawable.this.backgroundBitmap != null)
        {
          AnimatedFileDrawable.this.backgroundBitmap.recycle();
          AnimatedFileDrawable.access$502(AnimatedFileDrawable.this, null);
        }
      }
      do
      {
        return;
        AnimatedFileDrawable.access$602(AnimatedFileDrawable.this, null);
        AnimatedFileDrawable.access$702(AnimatedFileDrawable.this, AnimatedFileDrawable.this.backgroundBitmap);
        AnimatedFileDrawable.access$802(AnimatedFileDrawable.this, AnimatedFileDrawable.this.backgroundShader);
        if (AnimatedFileDrawable.this.metaData[3] < AnimatedFileDrawable.this.lastTimeStamp) {
          AnimatedFileDrawable.access$1102(AnimatedFileDrawable.this, 0);
        }
        if (AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp != 0) {
          AnimatedFileDrawable.access$1202(AnimatedFileDrawable.this, AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp);
        }
        AnimatedFileDrawable.access$1102(AnimatedFileDrawable.this, AnimatedFileDrawable.this.metaData[3]);
        if (AnimatedFileDrawable.this.secondParentView != null)
        {
          AnimatedFileDrawable.this.secondParentView.invalidate();
          return;
        }
      } while (AnimatedFileDrawable.this.parentView == null);
      AnimatedFileDrawable.this.parentView.invalidate();
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
  
  private static native int createDecoder(String paramString, int[] paramArrayOfInt);
  
  private static native void destroyDecoder(int paramInt);
  
  private static native int getVideoFrame(int paramInt, Bitmap paramBitmap, int[] paramArrayOfInt);
  
  protected static void runOnUiThread(Runnable paramRunnable)
  {
    if (Looper.myLooper() == uiHandler.getLooper())
    {
      paramRunnable.run();
      return;
    }
    uiHandler.post(paramRunnable);
  }
  
  private void scheduleNextGetFrame()
  {
    if ((this.loadFrameTask != null) || ((this.nativePtr == 0) && (this.decoderCreated)) || (this.destroyWhenDone)) {
      return;
    }
    Runnable localRunnable = this.loadFrameRunnable;
    this.loadFrameTask = localRunnable;
    postToDecodeQueue(localRunnable);
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (((this.nativePtr == 0) && (this.decoderCreated)) || (this.destroyWhenDone)) {
      return;
    }
    label47:
    int i;
    int j;
    float f;
    if (this.isRunning)
    {
      if ((this.renderingBitmap == null) && (this.nextRenderingBitmap == null)) {
        scheduleNextGetFrame();
      }
    }
    else
    {
      if (this.renderingBitmap == null) {
        break label498;
      }
      if (this.applyTransformation)
      {
        i = this.renderingBitmap.getWidth();
        int k = this.renderingBitmap.getHeight();
        int m;
        if (this.metaData[2] != 90)
        {
          m = k;
          j = i;
          if (this.metaData[2] != 270) {}
        }
        else
        {
          j = k;
          m = i;
        }
        this.dstRect.set(getBounds());
        this.scaleX = (this.dstRect.width() / j);
        this.scaleY = (this.dstRect.height() / m);
        this.applyTransformation = false;
      }
      if (this.roundRadius == 0) {
        break label588;
      }
      f = Math.max(this.scaleX, this.scaleY);
      if (this.renderingShader == null) {
        this.renderingShader = new BitmapShader(this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      }
      getPaint().setShader(this.renderingShader);
      this.roundRect.set(this.dstRect);
      this.shaderMatrix.reset();
      if (Math.abs(this.scaleX - this.scaleY) <= 1.0E-5F) {
        break label536;
      }
      if ((this.metaData[2] != 90) && (this.metaData[2] != 270)) {
        break label500;
      }
      j = (int)Math.floor(this.dstRect.height() / f);
      i = (int)Math.floor(this.dstRect.width() / f);
      label315:
      this.bitmapRect.set((this.renderingBitmap.getWidth() - j) / 2, (this.renderingBitmap.getHeight() - i) / 2, j, i);
      AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.metaData[2], Matrix.ScaleToFit.START);
    }
    for (;;)
    {
      this.renderingShader.setLocalMatrix(this.shaderMatrix);
      paramCanvas.drawRoundRect(this.roundRect, this.roundRadius, this.roundRadius, getPaint());
      label409:
      if (!this.isRunning) {
        break label667;
      }
      uiHandler.postDelayed(this.mInvalidateTask, this.invalidateAfter);
      return;
      if ((Math.abs(System.currentTimeMillis() - this.lastFrameTime) < this.invalidateAfter) || (this.nextRenderingBitmap == null)) {
        break label47;
      }
      scheduleNextGetFrame();
      this.renderingBitmap = this.nextRenderingBitmap;
      this.renderingShader = this.nextRenderingShader;
      this.nextRenderingBitmap = null;
      this.nextRenderingShader = null;
      this.lastFrameTime = System.currentTimeMillis();
      break label47;
      label498:
      break;
      label500:
      j = (int)Math.floor(this.dstRect.width() / f);
      i = (int)Math.floor(this.dstRect.height() / f);
      break label315;
      label536:
      this.bitmapRect.set(0.0F, 0.0F, this.renderingBitmap.getWidth(), this.renderingBitmap.getHeight());
      AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.metaData[2], Matrix.ScaleToFit.FILL);
    }
    label588:
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
      break label409;
      label667:
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
    if (this.renderingBitmap != null) {
      return this.renderingBitmap;
    }
    if (this.nextRenderingBitmap != null) {
      return this.nextRenderingBitmap;
    }
    return null;
  }
  
  public int getIntrinsicHeight()
  {
    if (this.decoderCreated)
    {
      if ((this.metaData[2] == 90) || (this.metaData[2] == 270)) {
        return this.metaData[0];
      }
      return this.metaData[1];
    }
    return AndroidUtilities.dp(100.0F);
  }
  
  public int getIntrinsicWidth()
  {
    if (this.decoderCreated)
    {
      if ((this.metaData[2] == 90) || (this.metaData[2] == 270)) {
        return this.metaData[1];
      }
      return this.metaData[0];
    }
    return AndroidUtilities.dp(100.0F);
  }
  
  public int getMinimumHeight()
  {
    if (this.decoderCreated)
    {
      if ((this.metaData[2] == 90) || (this.metaData[2] == 270)) {
        return this.metaData[0];
      }
      return this.metaData[1];
    }
    return AndroidUtilities.dp(100.0F);
  }
  
  public int getMinimumWidth()
  {
    if (this.decoderCreated)
    {
      if ((this.metaData[2] == 90) || (this.metaData[2] == 270)) {
        return this.metaData[1];
      }
      return this.metaData[0];
    }
    return AndroidUtilities.dp(100.0F);
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
    return (this.nativePtr != 0) && ((this.renderingBitmap != null) || (this.nextRenderingBitmap != null));
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
  
  protected void postToDecodeQueue(Runnable paramRunnable)
  {
    executor.execute(paramRunnable);
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
        if (this.nativePtr != 0)
        {
          destroyDecoder(this.nativePtr);
          this.nativePtr = 0;
        }
        if (this.nextRenderingBitmap != null)
        {
          this.nextRenderingBitmap.recycle();
          this.nextRenderingBitmap = null;
        }
      }
      while (this.renderingBitmap != null)
      {
        this.renderingBitmap.recycle();
        this.renderingBitmap = null;
        return;
        this.destroyWhenDone = true;
      }
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
    if (this.isRunning) {
      return;
    }
    this.isRunning = true;
    if (this.renderingBitmap == null) {
      scheduleNextGetFrame();
    }
    runOnUiThread(this.mStartTask);
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