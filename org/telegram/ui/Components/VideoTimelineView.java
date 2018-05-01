package org.telegram.ui.Components;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

@TargetApi(10)
public class VideoTimelineView
  extends View
{
  private static final Object sync = new Object();
  private AsyncTask<Integer, Integer, Bitmap> currentTask;
  private VideoTimelineViewDelegate delegate;
  private int frameHeight;
  private long frameTimeOffset;
  private int frameWidth;
  private ArrayList<Bitmap> frames = new ArrayList();
  private int framesToLoad;
  private boolean isRoundFrames;
  private float maxProgressDiff = 1.0F;
  private MediaMetadataRetriever mediaMetadataRetriever;
  private float minProgressDiff = 0.0F;
  private Paint paint = new Paint(1);
  private Paint paint2;
  private float pressDx;
  private boolean pressedLeft;
  private boolean pressedRight;
  private float progressLeft;
  private float progressRight = 1.0F;
  private Rect rect1;
  private Rect rect2;
  private long videoLength;
  
  public VideoTimelineView(Context paramContext)
  {
    super(paramContext);
    this.paint.setColor(-1);
    this.paint2 = new Paint();
    this.paint2.setColor(NUM);
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
              Bitmap localBitmap1 = VideoTimelineView.this.mediaMetadataRetriever.getFrameAtTime(VideoTimelineView.this.frameTimeOffset * this.frameNum * 1000L, 2);
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
                Bitmap localBitmap2 = Bitmap.createBitmap(VideoTimelineView.this.frameWidth, VideoTimelineView.this.frameHeight, localBitmap1.getConfig());
                paramAnonymousVarArgs = localBitmap1;
                Canvas localCanvas = new android/graphics/Canvas;
                paramAnonymousVarArgs = localBitmap1;
                localCanvas.<init>(localBitmap2);
                paramAnonymousVarArgs = localBitmap1;
                f1 = VideoTimelineView.this.frameWidth / localBitmap1.getWidth();
                paramAnonymousVarArgs = localBitmap1;
                f2 = VideoTimelineView.this.frameHeight / localBitmap1.getHeight();
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
                localRect2.<init>((VideoTimelineView.this.frameWidth - i) / 2, (VideoTimelineView.this.frameHeight - j) / 2, i, j);
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
            VideoTimelineView.this.frames.add(paramAnonymousBitmap);
            VideoTimelineView.this.invalidate();
            if (this.frameNum < VideoTimelineView.this.framesToLoad) {
              VideoTimelineView.this.reloadFrames(this.frameNum + 1);
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
  
  public float getRightProgress()
  {
    return this.progressRight;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = getMeasuredWidth() - AndroidUtilities.dp(36.0F);
    int j = (int)(i * this.progressLeft) + AndroidUtilities.dp(16.0F);
    int k = (int)(i * this.progressRight) + AndroidUtilities.dp(16.0F);
    paramCanvas.save();
    paramCanvas.clipRect(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(20.0F) + i, getMeasuredHeight());
    if ((this.frames.isEmpty()) && (this.currentTask == null))
    {
      reloadFrames(0);
      m = AndroidUtilities.dp(2.0F);
      paramCanvas.drawRect(AndroidUtilities.dp(16.0F), m, j, getMeasuredHeight() - m, this.paint2);
      paramCanvas.drawRect(AndroidUtilities.dp(4.0F) + k, m, AndroidUtilities.dp(16.0F) + i + AndroidUtilities.dp(4.0F), getMeasuredHeight() - m, this.paint2);
      paramCanvas.drawRect(j, 0.0F, AndroidUtilities.dp(2.0F) + j, getMeasuredHeight(), this.paint);
      paramCanvas.drawRect(AndroidUtilities.dp(2.0F) + k, 0.0F, AndroidUtilities.dp(4.0F) + k, getMeasuredHeight(), this.paint);
      paramCanvas.drawRect(AndroidUtilities.dp(2.0F) + j, 0.0F, AndroidUtilities.dp(4.0F) + k, m, this.paint);
      paramCanvas.drawRect(AndroidUtilities.dp(2.0F) + j, getMeasuredHeight() - m, AndroidUtilities.dp(4.0F) + k, getMeasuredHeight(), this.paint);
      paramCanvas.restore();
      paramCanvas.drawCircle(j, getMeasuredHeight() / 2, AndroidUtilities.dp(7.0F), this.paint);
      paramCanvas.drawCircle(AndroidUtilities.dp(4.0F) + k, getMeasuredHeight() / 2, AndroidUtilities.dp(7.0F), this.paint);
      return;
    }
    int n = 0;
    int m = 0;
    label349:
    Bitmap localBitmap;
    int i1;
    int i2;
    if (m < this.frames.size())
    {
      localBitmap = (Bitmap)this.frames.get(m);
      if (localBitmap != null)
      {
        i1 = AndroidUtilities.dp(16.0F);
        if (!this.isRoundFrames) {
          break label476;
        }
        i2 = this.frameWidth / 2;
        label402:
        i2 = i1 + i2 * n;
        i1 = AndroidUtilities.dp(2.0F);
        if (!this.isRoundFrames) {
          break label485;
        }
        this.rect2.set(i2, i1, AndroidUtilities.dp(28.0F) + i2, AndroidUtilities.dp(28.0F) + i1);
        paramCanvas.drawBitmap(localBitmap, this.rect1, this.rect2, null);
      }
    }
    for (;;)
    {
      n++;
      m++;
      break label349;
      break;
      label476:
      i2 = this.frameWidth;
      break label402;
      label485:
      paramCanvas.drawBitmap(localBitmap, i2, i1, null);
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramMotionEvent == null) {
      bool2 = bool1;
    }
    float f1;
    int i;
    int j;
    label407:
    label501:
    do
    {
      int m;
      do
      {
        for (;;)
        {
          return bool2;
          f1 = paramMotionEvent.getX();
          float f2 = paramMotionEvent.getY();
          i = getMeasuredWidth() - AndroidUtilities.dp(32.0F);
          j = (int)(i * this.progressLeft) + AndroidUtilities.dp(16.0F);
          k = (int)(i * this.progressRight) + AndroidUtilities.dp(16.0F);
          if (paramMotionEvent.getAction() == 0)
          {
            getParent().requestDisallowInterceptTouchEvent(true);
            bool2 = bool1;
            if (this.mediaMetadataRetriever != null)
            {
              m = AndroidUtilities.dp(12.0F);
              if ((j - m <= f1) && (f1 <= j + m) && (f2 >= 0.0F) && (f2 <= getMeasuredHeight()))
              {
                if (this.delegate != null) {
                  this.delegate.didStartDragging();
                }
                this.pressedLeft = true;
                this.pressDx = ((int)(f1 - j));
                invalidate();
                bool2 = true;
              }
              else
              {
                bool2 = bool1;
                if (k - m <= f1)
                {
                  bool2 = bool1;
                  if (f1 <= k + m)
                  {
                    bool2 = bool1;
                    if (f2 >= 0.0F)
                    {
                      bool2 = bool1;
                      if (f2 <= getMeasuredHeight())
                      {
                        if (this.delegate != null) {
                          this.delegate.didStartDragging();
                        }
                        this.pressedRight = true;
                        this.pressDx = ((int)(f1 - k));
                        invalidate();
                        bool2 = true;
                      }
                    }
                  }
                }
              }
            }
          }
          else
          {
            if ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3)) {
              break;
            }
            if (this.pressedLeft)
            {
              if (this.delegate != null) {
                this.delegate.didStopDragging();
              }
              this.pressedLeft = false;
              bool2 = true;
            }
            else
            {
              bool2 = bool1;
              if (this.pressedRight)
              {
                if (this.delegate != null) {
                  this.delegate.didStopDragging();
                }
                this.pressedRight = false;
                bool2 = true;
              }
            }
          }
        }
        bool2 = bool1;
      } while (paramMotionEvent.getAction() != 2);
      if (this.pressedLeft)
      {
        m = (int)(f1 - this.pressDx);
        if (m < AndroidUtilities.dp(16.0F))
        {
          j = AndroidUtilities.dp(16.0F);
          this.progressLeft = ((j - AndroidUtilities.dp(16.0F)) / i);
          if (this.progressRight - this.progressLeft <= this.maxProgressDiff) {
            break label501;
          }
          this.progressRight = (this.progressLeft + this.maxProgressDiff);
        }
        for (;;)
        {
          if (this.delegate != null) {
            this.delegate.onLeftProgressChanged(this.progressLeft);
          }
          invalidate();
          bool2 = true;
          break;
          j = m;
          if (m <= k) {
            break label407;
          }
          j = k;
          break label407;
          if ((this.minProgressDiff != 0.0F) && (this.progressRight - this.progressLeft < this.minProgressDiff))
          {
            this.progressLeft = (this.progressRight - this.minProgressDiff);
            if (this.progressLeft < 0.0F) {
              this.progressLeft = 0.0F;
            }
          }
        }
      }
      bool2 = bool1;
    } while (!this.pressedRight);
    int k = (int)(f1 - this.pressDx);
    if (k < j)
    {
      label583:
      this.progressRight = ((j - AndroidUtilities.dp(16.0F)) / i);
      if (this.progressRight - this.progressLeft <= this.maxProgressDiff) {
        break label689;
      }
      this.progressLeft = (this.progressRight - this.maxProgressDiff);
    }
    for (;;)
    {
      if (this.delegate != null) {
        this.delegate.onRightProgressChanged(this.progressRight);
      }
      invalidate();
      bool2 = true;
      break;
      j = k;
      if (k <= AndroidUtilities.dp(16.0F) + i) {
        break label583;
      }
      j = i + AndroidUtilities.dp(16.0F);
      break label583;
      label689:
      if ((this.minProgressDiff != 0.0F) && (this.progressRight - this.progressLeft < this.minProgressDiff))
      {
        this.progressRight = (this.progressLeft + this.minProgressDiff);
        if (this.progressRight > 1.0F) {
          this.progressRight = 1.0F;
        }
      }
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
    
    public abstract void onRightProgressChanged(float paramFloat);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/VideoTimelineView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */