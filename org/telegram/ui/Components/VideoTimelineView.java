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
import java.util.Iterator;
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
  private MediaMetadataRetriever mediaMetadataRetriever;
  private Paint paint = new Paint(1);
  private Paint paint2;
  private float pressDx;
  private boolean pressedLeft;
  private boolean pressedRight;
  private float progressLeft;
  private float progressRight = 1.0F;
  private long videoLength;
  
  public VideoTimelineView(Context paramContext)
  {
    super(paramContext);
    this.paint.setColor(-1);
    this.paint2 = new Paint();
    this.paint2.setColor(2130706432);
  }
  
  private void reloadFrames(int paramInt)
  {
    if (this.mediaMetadataRetriever == null) {
      return;
    }
    if (paramInt == 0)
    {
      this.frameHeight = AndroidUtilities.dp(40.0F);
      this.framesToLoad = ((getMeasuredWidth() - AndroidUtilities.dp(16.0F)) / this.frameHeight);
      this.frameWidth = ((int)Math.ceil((getMeasuredWidth() - AndroidUtilities.dp(16.0F)) / this.framesToLoad));
      this.frameTimeOffset = (this.videoLength / this.framesToLoad);
    }
    this.currentTask = new AsyncTask()
    {
      private int frameNum = 0;
      
      protected Bitmap doInBackground(Integer... paramAnonymousVarArgs)
      {
        this.frameNum = paramAnonymousVarArgs[0].intValue();
        paramAnonymousVarArgs = null;
        if (isCancelled()) {
          return null;
        }
        for (;;)
        {
          try
          {
            Bitmap localBitmap1 = VideoTimelineView.this.mediaMetadataRetriever.getFrameAtTime(VideoTimelineView.this.frameTimeOffset * this.frameNum * 1000L);
            paramAnonymousVarArgs = localBitmap1;
            if (isCancelled()) {
              return null;
            }
            paramAnonymousVarArgs = localBitmap1;
            if (localBitmap1 != null)
            {
              paramAnonymousVarArgs = localBitmap1;
              Bitmap localBitmap2 = Bitmap.createBitmap(VideoTimelineView.this.frameWidth, VideoTimelineView.this.frameHeight, localBitmap1.getConfig());
              paramAnonymousVarArgs = localBitmap1;
              Canvas localCanvas = new Canvas(localBitmap2);
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
              localCanvas.drawBitmap(localBitmap1, new Rect(0, 0, localBitmap1.getWidth(), localBitmap1.getHeight()), new Rect((VideoTimelineView.this.frameWidth - i) / 2, (VideoTimelineView.this.frameHeight - j) / 2, i, j), null);
              paramAnonymousVarArgs = localBitmap1;
              localBitmap1.recycle();
              paramAnonymousVarArgs = localBitmap2;
            }
          }
          catch (Exception localException)
          {
            float f1;
            float f2;
            FileLog.e("tmessages", localException);
            continue;
          }
          return paramAnonymousVarArgs;
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
  }
  
  public void clearFrames()
  {
    Iterator localIterator = this.frames.iterator();
    while (localIterator.hasNext())
    {
      Bitmap localBitmap = (Bitmap)localIterator.next();
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
        ??? = this.frames.iterator();
        while (((Iterator)???).hasNext())
        {
          Bitmap localBitmap = (Bitmap)((Iterator)???).next();
          if (localBitmap != null) {
            localBitmap.recycle();
          }
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
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
    int k = getMeasuredWidth() - AndroidUtilities.dp(36.0F);
    int m = (int)(k * this.progressLeft) + AndroidUtilities.dp(16.0F);
    int n = (int)(k * this.progressRight) + AndroidUtilities.dp(16.0F);
    paramCanvas.save();
    paramCanvas.clipRect(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(20.0F) + k, AndroidUtilities.dp(44.0F));
    if ((this.frames.isEmpty()) && (this.currentTask == null)) {
      reloadFrames(0);
    }
    for (;;)
    {
      paramCanvas.drawRect(AndroidUtilities.dp(16.0F), AndroidUtilities.dp(2.0F), m, AndroidUtilities.dp(42.0F), this.paint2);
      paramCanvas.drawRect(AndroidUtilities.dp(4.0F) + n, AndroidUtilities.dp(2.0F), AndroidUtilities.dp(16.0F) + k + AndroidUtilities.dp(4.0F), AndroidUtilities.dp(42.0F), this.paint2);
      paramCanvas.drawRect(m, 0.0F, AndroidUtilities.dp(2.0F) + m, AndroidUtilities.dp(44.0F), this.paint);
      paramCanvas.drawRect(AndroidUtilities.dp(2.0F) + n, 0.0F, AndroidUtilities.dp(4.0F) + n, AndroidUtilities.dp(44.0F), this.paint);
      paramCanvas.drawRect(AndroidUtilities.dp(2.0F) + m, 0.0F, AndroidUtilities.dp(4.0F) + n, AndroidUtilities.dp(2.0F), this.paint);
      paramCanvas.drawRect(AndroidUtilities.dp(2.0F) + m, AndroidUtilities.dp(42.0F), AndroidUtilities.dp(4.0F) + n, AndroidUtilities.dp(44.0F), this.paint);
      paramCanvas.restore();
      paramCanvas.drawCircle(m, getMeasuredHeight() / 2, AndroidUtilities.dp(7.0F), this.paint);
      paramCanvas.drawCircle(AndroidUtilities.dp(4.0F) + n, getMeasuredHeight() / 2, AndroidUtilities.dp(7.0F), this.paint);
      return;
      int j = 0;
      int i = 0;
      while (i < this.frames.size())
      {
        Bitmap localBitmap = (Bitmap)this.frames.get(i);
        if (localBitmap != null) {
          paramCanvas.drawBitmap(localBitmap, AndroidUtilities.dp(16.0F) + this.frameWidth * j, AndroidUtilities.dp(2.0F), null);
        }
        j += 1;
        i += 1;
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent == null) {}
    float f1;
    int m;
    int i;
    do
    {
      int k;
      do
      {
        do
        {
          float f2;
          do
          {
            return false;
            f1 = paramMotionEvent.getX();
            f2 = paramMotionEvent.getY();
            m = getMeasuredWidth() - AndroidUtilities.dp(32.0F);
            i = (int)(m * this.progressLeft) + AndroidUtilities.dp(16.0F);
            j = (int)(m * this.progressRight) + AndroidUtilities.dp(16.0F);
            if (paramMotionEvent.getAction() != 0) {
              break;
            }
            k = AndroidUtilities.dp(12.0F);
            if ((i - k <= f1) && (f1 <= i + k) && (f2 >= 0.0F) && (f2 <= getMeasuredHeight()))
            {
              this.pressedLeft = true;
              this.pressDx = ((int)(f1 - i));
              getParent().requestDisallowInterceptTouchEvent(true);
              invalidate();
              return true;
            }
          } while ((j - k > f1) || (f1 > j + k) || (f2 < 0.0F) || (f2 > getMeasuredHeight()));
          this.pressedRight = true;
          this.pressDx = ((int)(f1 - j));
          getParent().requestDisallowInterceptTouchEvent(true);
          invalidate();
          return true;
          if ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3)) {
            break;
          }
          if (this.pressedLeft)
          {
            this.pressedLeft = false;
            return true;
          }
        } while (!this.pressedRight);
        this.pressedRight = false;
        return true;
      } while (paramMotionEvent.getAction() != 2);
      if (this.pressedLeft)
      {
        k = (int)(f1 - this.pressDx);
        if (k < AndroidUtilities.dp(16.0F)) {
          i = AndroidUtilities.dp(16.0F);
        }
        for (;;)
        {
          this.progressLeft = ((i - AndroidUtilities.dp(16.0F)) / m);
          if (this.delegate != null) {
            this.delegate.onLeftProgressChanged(this.progressLeft);
          }
          invalidate();
          return true;
          i = k;
          if (k > j) {
            i = j;
          }
        }
      }
    } while (!this.pressedRight);
    int j = (int)(f1 - this.pressDx);
    if (j < i) {}
    for (;;)
    {
      this.progressRight = ((i - AndroidUtilities.dp(16.0F)) / m);
      if (this.delegate != null) {
        this.delegate.onRifhtProgressChanged(this.progressRight);
      }
      invalidate();
      return true;
      i = j;
      if (j > AndroidUtilities.dp(16.0F) + m) {
        i = m + AndroidUtilities.dp(16.0F);
      }
    }
  }
  
  public void setDelegate(VideoTimelineViewDelegate paramVideoTimelineViewDelegate)
  {
    this.delegate = paramVideoTimelineViewDelegate;
  }
  
  public void setVideoPath(String paramString)
  {
    this.mediaMetadataRetriever = new MediaMetadataRetriever();
    try
    {
      this.mediaMetadataRetriever.setDataSource(paramString);
      this.videoLength = Long.parseLong(this.mediaMetadataRetriever.extractMetadata(9));
      return;
    }
    catch (Exception paramString)
    {
      FileLog.e("tmessages", paramString);
    }
  }
  
  public static abstract interface VideoTimelineViewDelegate
  {
    public abstract void onLeftProgressChanged(float paramFloat);
    
    public abstract void onRifhtProgressChanged(float paramFloat);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/VideoTimelineView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */