package org.telegram.ui.Components;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewParent;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

@TargetApi(10)
public class VideoTimelinePlayView
  extends View
{
  private static final Object sync = new Object();
  private float bufferedProgress = 0.5F;
  private AsyncTask<Integer, Integer, Bitmap> currentTask;
  private VideoTimelineViewDelegate delegate;
  private Drawable drawableLeft;
  private Drawable drawableRight;
  private int frameHeight;
  private long frameTimeOffset;
  private int frameWidth;
  private ArrayList<Bitmap> frames = new ArrayList();
  private int framesToLoad;
  private boolean isRoundFrames;
  private int lastWidth;
  private float maxProgressDiff = 1.0F;
  private MediaMetadataRetriever mediaMetadataRetriever;
  private float minProgressDiff = 0.0F;
  private Paint paint = new Paint(1);
  private Paint paint2;
  private float playProgress = 0.5F;
  private float pressDx;
  private boolean pressedLeft;
  private boolean pressedPlay;
  private boolean pressedRight;
  private float progressLeft;
  private float progressRight = 1.0F;
  private Rect rect1;
  private Rect rect2;
  private RectF rect3 = new RectF();
  private long videoLength;
  
  public VideoTimelinePlayView(Context paramContext)
  {
    super(paramContext);
    this.paint.setColor(-1);
    this.paint2 = new Paint();
    this.paint2.setColor(NUM);
    this.drawableLeft = paramContext.getResources().getDrawable(NUM);
    this.drawableLeft.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
    this.drawableRight = paramContext.getResources().getDrawable(NUM);
    this.drawableRight.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
  }
  
  private void reloadFrames(int paramInt)
  {
    if (this.mediaMetadataRetriever == null) {
      return;
    }
    if (paramInt == 0)
    {
      if (!this.isRoundFrames) {
        break label122;
      }
      int i = AndroidUtilities.dp(56.0F);
      this.frameWidth = i;
      this.frameHeight = i;
      this.framesToLoad = ((int)Math.ceil((getMeasuredWidth() - AndroidUtilities.dp(16.0F)) / (this.frameHeight / 2.0F)));
    }
    for (;;)
    {
      this.frameTimeOffset = (this.videoLength / this.framesToLoad);
      this.currentTask = new AsyncTask()
      {
        private int frameNum = 0;
        
        protected Bitmap doInBackground(Integer... paramAnonymousVarArgs)
        {
          this.frameNum = paramAnonymousVarArgs[0].intValue();
          paramAnonymousVarArgs = null;
          if (isCancelled())
          {
            paramAnonymousVarArgs = null;
            return paramAnonymousVarArgs;
          }
          for (;;)
          {
            try
            {
              Bitmap localBitmap1 = VideoTimelinePlayView.this.mediaMetadataRetriever.getFrameAtTime(VideoTimelinePlayView.this.frameTimeOffset * this.frameNum * 1000L, 2);
              paramAnonymousVarArgs = localBitmap1;
              if (isCancelled())
              {
                paramAnonymousVarArgs = null;
                break;
              }
              paramAnonymousVarArgs = localBitmap1;
              if (localBitmap1 != null)
              {
                paramAnonymousVarArgs = localBitmap1;
                Bitmap localBitmap2 = Bitmap.createBitmap(VideoTimelinePlayView.this.frameWidth, VideoTimelinePlayView.this.frameHeight, localBitmap1.getConfig());
                paramAnonymousVarArgs = localBitmap1;
                Canvas localCanvas = new android/graphics/Canvas;
                paramAnonymousVarArgs = localBitmap1;
                localCanvas.<init>(localBitmap2);
                paramAnonymousVarArgs = localBitmap1;
                f1 = VideoTimelinePlayView.this.frameWidth / localBitmap1.getWidth();
                paramAnonymousVarArgs = localBitmap1;
                f2 = VideoTimelinePlayView.this.frameHeight / localBitmap1.getHeight();
                if (f1 <= f2) {
                  continue;
                }
                paramAnonymousVarArgs = localBitmap1;
                int i = (int)(localBitmap1.getWidth() * f1);
                paramAnonymousVarArgs = localBitmap1;
                int j = (int)(localBitmap1.getHeight() * f1);
                paramAnonymousVarArgs = localBitmap1;
                Rect localRect1 = new android/graphics/Rect;
                paramAnonymousVarArgs = localBitmap1;
                localRect1.<init>(0, 0, localBitmap1.getWidth(), localBitmap1.getHeight());
                paramAnonymousVarArgs = localBitmap1;
                Rect localRect2 = new android/graphics/Rect;
                paramAnonymousVarArgs = localBitmap1;
                localRect2.<init>((VideoTimelinePlayView.this.frameWidth - i) / 2, (VideoTimelinePlayView.this.frameHeight - j) / 2, i, j);
                paramAnonymousVarArgs = localBitmap1;
                localCanvas.drawBitmap(localBitmap1, localRect1, localRect2, null);
                paramAnonymousVarArgs = localBitmap1;
                localBitmap1.recycle();
                paramAnonymousVarArgs = localBitmap2;
              }
            }
            catch (Exception localException)
            {
              float f1;
              float f2;
              FileLog.e(localException);
              continue;
            }
            f1 = f2;
          }
        }
        
        protected void onPostExecute(Bitmap paramAnonymousBitmap)
        {
          if (!isCancelled())
          {
            VideoTimelinePlayView.this.frames.add(paramAnonymousBitmap);
            VideoTimelinePlayView.this.invalidate();
            if (this.frameNum < VideoTimelinePlayView.this.framesToLoad) {
              VideoTimelinePlayView.this.reloadFrames(this.frameNum + 1);
            }
          }
        }
      };
      this.currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Integer[] { Integer.valueOf(paramInt), null, null });
      break;
      label122:
      this.frameHeight = AndroidUtilities.dp(40.0F);
      this.framesToLoad = ((getMeasuredWidth() - AndroidUtilities.dp(16.0F)) / this.frameHeight);
      this.frameWidth = ((int)Math.ceil((getMeasuredWidth() - AndroidUtilities.dp(16.0F)) / this.framesToLoad));
    }
  }
  
  public void clearFrames()
  {
    for (int i = 0; i < this.frames.size(); i++)
    {
      Bitmap localBitmap = (Bitmap)this.frames.get(i);
      if (localBitmap != null) {
        localBitmap.recycle();
      }
    }
    this.frames.clear();
    if (this.currentTask != null)
    {
      this.currentTask.cancel(true);
      this.currentTask = null;
    }
    invalidate();
  }
  
  public void destroy()
  {
    synchronized (sync)
    {
      try
      {
        if (this.mediaMetadataRetriever != null)
        {
          this.mediaMetadataRetriever.release();
          this.mediaMetadataRetriever = null;
        }
        for (int i = 0; i < this.frames.size(); i++)
        {
          ??? = (Bitmap)this.frames.get(i);
          if (??? != null) {
            ((Bitmap)???).recycle();
          }
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
    this.frames.clear();
    if (this.currentTask != null)
    {
      this.currentTask.cancel(true);
      this.currentTask = null;
    }
  }
  
  public float getLeftProgress()
  {
    return this.progressLeft;
  }
  
  public float getProgress()
  {
    return this.playProgress;
  }
  
  public float getRightProgress()
  {
    return this.progressRight;
  }
  
  public boolean isDragging()
  {
    return this.pressedPlay;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = getMeasuredWidth() - AndroidUtilities.dp(36.0F);
    int j = (int)(i * this.progressLeft) + AndroidUtilities.dp(16.0F);
    int k = (int)(i * this.progressRight) + AndroidUtilities.dp(16.0F);
    paramCanvas.save();
    paramCanvas.clipRect(AndroidUtilities.dp(16.0F), AndroidUtilities.dp(4.0F), AndroidUtilities.dp(20.0F) + i, AndroidUtilities.dp(48.0F));
    if ((this.frames.isEmpty()) && (this.currentTask == null))
    {
      reloadFrames(0);
      m = AndroidUtilities.dp(6.0F);
      n = AndroidUtilities.dp(48.0F);
      paramCanvas.drawRect(AndroidUtilities.dp(16.0F), m, j, AndroidUtilities.dp(46.0F), this.paint2);
      paramCanvas.drawRect(AndroidUtilities.dp(4.0F) + k, m, AndroidUtilities.dp(16.0F) + i + AndroidUtilities.dp(4.0F), AndroidUtilities.dp(46.0F), this.paint2);
      paramCanvas.drawRect(j, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) + j, n, this.paint);
      paramCanvas.drawRect(AndroidUtilities.dp(2.0F) + k, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F) + k, n, this.paint);
      paramCanvas.drawRect(AndroidUtilities.dp(2.0F) + j, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F) + k, m, this.paint);
      paramCanvas.drawRect(AndroidUtilities.dp(2.0F) + j, n - AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) + k, n, this.paint);
      paramCanvas.restore();
      this.rect3.set(j - AndroidUtilities.dp(8.0F), AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) + j, n);
      paramCanvas.drawRoundRect(this.rect3, AndroidUtilities.dp(2.0F), AndroidUtilities.dp(2.0F), this.paint);
      this.drawableLeft.setBounds(j - AndroidUtilities.dp(8.0F), AndroidUtilities.dp(4.0F) + (AndroidUtilities.dp(44.0F) - AndroidUtilities.dp(18.0F)) / 2, AndroidUtilities.dp(2.0F) + j, (AndroidUtilities.dp(44.0F) - AndroidUtilities.dp(18.0F)) / 2 + AndroidUtilities.dp(22.0F));
      this.drawableLeft.draw(paramCanvas);
      this.rect3.set(AndroidUtilities.dp(2.0F) + k, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(12.0F) + k, n);
      paramCanvas.drawRoundRect(this.rect3, AndroidUtilities.dp(2.0F), AndroidUtilities.dp(2.0F), this.paint);
      this.drawableRight.setBounds(AndroidUtilities.dp(2.0F) + k, AndroidUtilities.dp(4.0F) + (AndroidUtilities.dp(44.0F) - AndroidUtilities.dp(18.0F)) / 2, AndroidUtilities.dp(12.0F) + k, (AndroidUtilities.dp(44.0F) - AndroidUtilities.dp(18.0F)) / 2 + AndroidUtilities.dp(22.0F));
      this.drawableRight.draw(paramCanvas);
      float f = AndroidUtilities.dp(18.0F) + i * (this.progressLeft + (this.progressRight - this.progressLeft) * this.playProgress);
      this.rect3.set(f - AndroidUtilities.dp(1.5F), AndroidUtilities.dp(2.0F), AndroidUtilities.dp(1.5F) + f, AndroidUtilities.dp(50.0F));
      paramCanvas.drawRoundRect(this.rect3, AndroidUtilities.dp(1.0F), AndroidUtilities.dp(1.0F), this.paint2);
      paramCanvas.drawCircle(f, AndroidUtilities.dp(52.0F), AndroidUtilities.dp(3.5F), this.paint2);
      this.rect3.set(f - AndroidUtilities.dp(1.0F), AndroidUtilities.dp(2.0F), AndroidUtilities.dp(1.0F) + f, AndroidUtilities.dp(50.0F));
      paramCanvas.drawRoundRect(this.rect3, AndroidUtilities.dp(1.0F), AndroidUtilities.dp(1.0F), this.paint);
      paramCanvas.drawCircle(f, AndroidUtilities.dp(52.0F), AndroidUtilities.dp(3.0F), this.paint);
      return;
    }
    int m = 0;
    int n = 0;
    label788:
    Bitmap localBitmap;
    int i1;
    int i2;
    if (n < this.frames.size())
    {
      localBitmap = (Bitmap)this.frames.get(n);
      if (localBitmap != null)
      {
        i1 = AndroidUtilities.dp(16.0F);
        if (!this.isRoundFrames) {
          break label919;
        }
        i2 = this.frameWidth / 2;
        label841:
        i2 = i1 + i2 * m;
        i1 = AndroidUtilities.dp(6.0F);
        if (!this.isRoundFrames) {
          break label928;
        }
        this.rect2.set(i2, i1, AndroidUtilities.dp(28.0F) + i2, AndroidUtilities.dp(28.0F) + i1);
        paramCanvas.drawBitmap(localBitmap, this.rect1, this.rect2, null);
      }
    }
    for (;;)
    {
      m++;
      n++;
      break label788;
      break;
      label919:
      i2 = this.frameWidth;
      break label841;
      label928:
      paramCanvas.drawBitmap(localBitmap, i2, i1, null);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    if (this.lastWidth != paramInt1)
    {
      clearFrames();
      this.lastWidth = paramInt1;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if (paramMotionEvent == null) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      int i = getMeasuredWidth() - AndroidUtilities.dp(32.0F);
      int j = (int)(i * this.progressLeft) + AndroidUtilities.dp(16.0F);
      int k = (int)(i * (this.progressLeft + (this.progressRight - this.progressLeft) * this.playProgress)) + AndroidUtilities.dp(16.0F);
      int m = (int)(i * this.progressRight) + AndroidUtilities.dp(16.0F);
      if (paramMotionEvent.getAction() == 0)
      {
        getParent().requestDisallowInterceptTouchEvent(true);
        if (this.mediaMetadataRetriever == null)
        {
          bool = false;
          continue;
        }
        i = AndroidUtilities.dp(12.0F);
        int n = AndroidUtilities.dp(8.0F);
        if ((k - n <= f1) && (f1 <= k + n) && (f2 >= 0.0F) && (f2 <= getMeasuredHeight()))
        {
          if (this.delegate != null) {
            this.delegate.didStartDragging();
          }
          this.pressedPlay = true;
          this.pressDx = ((int)(f1 - k));
          invalidate();
          bool = true;
          continue;
        }
        if ((j - i <= f1) && (f1 <= j + i) && (f2 >= 0.0F) && (f2 <= getMeasuredHeight()))
        {
          if (this.delegate != null) {
            this.delegate.didStartDragging();
          }
          this.pressedLeft = true;
          this.pressDx = ((int)(f1 - j));
          invalidate();
          bool = true;
          continue;
        }
        if ((m - i <= f1) && (f1 <= m + i) && (f2 >= 0.0F) && (f2 <= getMeasuredHeight()))
        {
          if (this.delegate != null) {
            this.delegate.didStartDragging();
          }
          this.pressedRight = true;
          this.pressDx = ((int)(f1 - m));
          invalidate();
          bool = true;
        }
      }
      else if ((paramMotionEvent.getAction() == 1) || (paramMotionEvent.getAction() == 3))
      {
        if (this.pressedLeft)
        {
          if (this.delegate != null) {
            this.delegate.didStopDragging();
          }
          this.pressedLeft = false;
          bool = true;
          continue;
        }
        if (this.pressedRight)
        {
          if (this.delegate != null) {
            this.delegate.didStopDragging();
          }
          this.pressedRight = false;
          bool = true;
          continue;
        }
        if (this.pressedPlay)
        {
          if (this.delegate != null) {
            this.delegate.didStopDragging();
          }
          this.pressedPlay = false;
          bool = true;
        }
      }
      else if (paramMotionEvent.getAction() == 2)
      {
        if (this.pressedPlay)
        {
          this.playProgress = (((int)(f1 - this.pressDx) - AndroidUtilities.dp(16.0F)) / i);
          if (this.playProgress < this.progressLeft) {
            this.playProgress = this.progressLeft;
          }
          for (;;)
          {
            this.playProgress = ((this.playProgress - this.progressLeft) / (this.progressRight - this.progressLeft));
            if (this.delegate != null) {
              this.delegate.onPlayProgressChanged(this.progressLeft + (this.progressRight - this.progressLeft) * this.playProgress);
            }
            invalidate();
            bool = true;
            break;
            if (this.playProgress > this.progressRight) {
              this.playProgress = this.progressRight;
            }
          }
        }
        if (this.pressedLeft)
        {
          k = (int)(f1 - this.pressDx);
          if (k < AndroidUtilities.dp(16.0F))
          {
            j = AndroidUtilities.dp(16.0F);
            label681:
            this.progressLeft = ((j - AndroidUtilities.dp(16.0F)) / i);
            if (this.progressRight - this.progressLeft <= this.maxProgressDiff) {
              break label775;
            }
            this.progressRight = (this.progressLeft + this.maxProgressDiff);
          }
          for (;;)
          {
            if (this.delegate != null) {
              this.delegate.onLeftProgressChanged(this.progressLeft);
            }
            invalidate();
            bool = true;
            break;
            j = k;
            if (k <= m) {
              break label681;
            }
            j = m;
            break label681;
            label775:
            if ((this.minProgressDiff != 0.0F) && (this.progressRight - this.progressLeft < this.minProgressDiff))
            {
              this.progressLeft = (this.progressRight - this.minProgressDiff);
              if (this.progressLeft < 0.0F) {
                this.progressLeft = 0.0F;
              }
            }
          }
        }
        if (this.pressedRight)
        {
          m = (int)(f1 - this.pressDx);
          if (m < j)
          {
            label854:
            this.progressRight = ((j - AndroidUtilities.dp(16.0F)) / i);
            if (this.progressRight - this.progressLeft <= this.maxProgressDiff) {
              break label960;
            }
            this.progressLeft = (this.progressRight - this.maxProgressDiff);
          }
          for (;;)
          {
            if (this.delegate != null) {
              this.delegate.onRightProgressChanged(this.progressRight);
            }
            invalidate();
            bool = true;
            break;
            j = m;
            if (m <= AndroidUtilities.dp(16.0F) + i) {
              break label854;
            }
            j = i + AndroidUtilities.dp(16.0F);
            break label854;
            label960:
            if ((this.minProgressDiff != 0.0F) && (this.progressRight - this.progressLeft < this.minProgressDiff))
            {
              this.progressRight = (this.progressLeft + this.minProgressDiff);
              if (this.progressRight > 1.0F) {
                this.progressRight = 1.0F;
              }
            }
          }
        }
      }
      bool = false;
    }
  }
  
  public void setColor(int paramInt)
  {
    this.paint.setColor(paramInt);
  }
  
  public void setDelegate(VideoTimelineViewDelegate paramVideoTimelineViewDelegate)
  {
    this.delegate = paramVideoTimelineViewDelegate;
  }
  
  public void setMaxProgressDiff(float paramFloat)
  {
    this.maxProgressDiff = paramFloat;
    if (this.progressRight - this.progressLeft > this.maxProgressDiff)
    {
      this.progressRight = (this.progressLeft + this.maxProgressDiff);
      invalidate();
    }
  }
  
  public void setMinProgressDiff(float paramFloat)
  {
    this.minProgressDiff = paramFloat;
  }
  
  public void setProgress(float paramFloat)
  {
    this.playProgress = paramFloat;
    invalidate();
  }
  
  public void setRoundFrames(boolean paramBoolean)
  {
    this.isRoundFrames = paramBoolean;
    if (this.isRoundFrames)
    {
      this.rect1 = new Rect(AndroidUtilities.dp(14.0F), AndroidUtilities.dp(14.0F), AndroidUtilities.dp(42.0F), AndroidUtilities.dp(42.0F));
      this.rect2 = new Rect();
    }
  }
  
  public void setVideoPath(String paramString)
  {
    destroy();
    this.mediaMetadataRetriever = new MediaMetadataRetriever();
    this.progressLeft = 0.0F;
    this.progressRight = 1.0F;
    try
    {
      this.mediaMetadataRetriever.setDataSource(paramString);
      this.videoLength = Long.parseLong(this.mediaMetadataRetriever.extractMetadata(9));
      invalidate();
      return;
    }
    catch (Exception paramString)
    {
      for (;;)
      {
        FileLog.e(paramString);
      }
    }
  }
  
  public static abstract interface VideoTimelineViewDelegate
  {
    public abstract void didStartDragging();
    
    public abstract void didStopDragging();
    
    public abstract void onLeftProgressChanged(float paramFloat);
    
    public abstract void onPlayProgressChanged(float paramFloat);
    
    public abstract void onRightProgressChanged(float paramFloat);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/VideoTimelinePlayView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */