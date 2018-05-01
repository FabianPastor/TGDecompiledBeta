package net.hockeyapp.android.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.widget.ImageView;
import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.ImageUtils;

@SuppressLint({"ViewConstructor"})
public class PaintView
  extends ImageView
{
  private float mX;
  private float mY;
  private Paint paint = new Paint();
  private Path path = new Path();
  private Stack<Path> paths = new Stack();
  
  @SuppressLint({"StaticFieldLeak"})
  public PaintView(Context paramContext, Uri paramUri, int paramInt1, int paramInt2)
  {
    super(paramContext);
    this.paint.setAntiAlias(true);
    this.paint.setDither(true);
    this.paint.setColor(-65536);
    this.paint.setStyle(Paint.Style.STROKE);
    this.paint.setStrokeJoin(Paint.Join.ROUND);
    this.paint.setStrokeCap(Paint.Cap.ROUND);
    this.paint.setStrokeWidth(12.0F);
    new AsyncTask()
    {
      protected Bitmap doInBackground(Object... paramAnonymousVarArgs)
      {
        Context localContext = (Context)paramAnonymousVarArgs[0];
        Uri localUri = (Uri)paramAnonymousVarArgs[1];
        Integer localInteger = (Integer)paramAnonymousVarArgs[2];
        paramAnonymousVarArgs = (Integer)paramAnonymousVarArgs[3];
        try
        {
          paramAnonymousVarArgs = ImageUtils.decodeSampledBitmap(localContext, localUri, localInteger.intValue(), paramAnonymousVarArgs.intValue());
          return paramAnonymousVarArgs;
        }
        catch (IOException paramAnonymousVarArgs)
        {
          for (;;)
          {
            HockeyLog.error("Could not load image into ImageView.", paramAnonymousVarArgs);
            paramAnonymousVarArgs = null;
          }
        }
      }
      
      protected void onPostExecute(Bitmap paramAnonymousBitmap)
      {
        if (paramAnonymousBitmap == null) {}
        for (;;)
        {
          return;
          PaintView.this.setImageBitmap(paramAnonymousBitmap);
        }
      }
      
      protected void onPreExecute()
      {
        PaintView.this.setAdjustViewBounds(true);
      }
    }.execute(new Object[] { paramContext, paramUri, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
  }
  
  private void touchMove(float paramFloat1, float paramFloat2)
  {
    float f1 = Math.abs(paramFloat1 - this.mX);
    float f2 = Math.abs(paramFloat2 - this.mY);
    if ((f1 >= 4.0F) || (f2 >= 4.0F))
    {
      this.path.quadTo(this.mX, this.mY, (this.mX + paramFloat1) / 2.0F, (this.mY + paramFloat2) / 2.0F);
      this.mX = paramFloat1;
      this.mY = paramFloat2;
    }
  }
  
  private void touchStart(float paramFloat1, float paramFloat2)
  {
    this.path.reset();
    this.path.moveTo(paramFloat1, paramFloat2);
    this.mX = paramFloat1;
    this.mY = paramFloat2;
  }
  
  private void touchUp()
  {
    this.path.lineTo(this.mX, this.mY);
    this.paths.push(this.path);
    this.path = new Path();
  }
  
  public void clearImage()
  {
    this.paths.clear();
    invalidate();
  }
  
  public boolean isClear()
  {
    return this.paths.empty();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    Iterator localIterator = this.paths.iterator();
    while (localIterator.hasNext()) {
      paramCanvas.drawPath((Path)localIterator.next(), this.paint);
    }
    paramCanvas.drawPath(this.path, this.paint);
  }
  
  @SuppressLint({"ClickableViewAccessibility"})
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    switch (paramMotionEvent.getAction())
    {
    }
    for (;;)
    {
      return true;
      touchStart(f1, f2);
      invalidate();
      continue;
      touchMove(f1, f2);
      invalidate();
      continue;
      touchUp();
      invalidate();
    }
  }
  
  public void undo()
  {
    if (!this.paths.empty())
    {
      this.paths.pop();
      invalidate();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/views/PaintView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */