package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;

public class PhotoFilterCurvesControl
  extends View
{
  private static final int CurvesSegmentBlacks = 1;
  private static final int CurvesSegmentHighlights = 4;
  private static final int CurvesSegmentMidtones = 3;
  private static final int CurvesSegmentNone = 0;
  private static final int CurvesSegmentShadows = 2;
  private static final int CurvesSegmentWhites = 5;
  private static final int GestureStateBegan = 1;
  private static final int GestureStateCancelled = 4;
  private static final int GestureStateChanged = 2;
  private static final int GestureStateEnded = 3;
  private static final int GestureStateFailed = 5;
  private int activeSegment = 0;
  private Rect actualArea = new Rect();
  private boolean checkForMoving = true;
  private PhotoFilterView.CurvesToolValue curveValue;
  private PhotoFilterCurvesControlDelegate delegate;
  private boolean isMoving;
  private float lastX;
  private float lastY;
  private Paint paint = new Paint(1);
  private Paint paintCurve = new Paint(1);
  private Paint paintDash = new Paint(1);
  private Path path = new Path();
  private TextPaint textPaint = new TextPaint(1);
  
  public PhotoFilterCurvesControl(Context paramContext, PhotoFilterView.CurvesToolValue paramCurvesToolValue)
  {
    super(paramContext);
    setWillNotDraw(false);
    this.curveValue = paramCurvesToolValue;
    this.paint.setColor(-NUM);
    this.paint.setStrokeWidth(AndroidUtilities.dp(1.0F));
    this.paint.setStyle(Paint.Style.STROKE);
    this.paintDash.setColor(-NUM);
    this.paintDash.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.paintDash.setStyle(Paint.Style.STROKE);
    this.paintCurve.setColor(-1);
    this.paintCurve.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.paintCurve.setStyle(Paint.Style.STROKE);
    this.textPaint.setColor(-4210753);
    this.textPaint.setTextSize(AndroidUtilities.dp(13.0F));
  }
  
  private void handlePan(int paramInt, MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    switch (paramInt)
    {
    }
    for (;;)
    {
      return;
      selectSegmentWithPoint(f1);
      continue;
      float f3 = Math.min(2.0F, (this.lastY - f2) / 8.0F);
      paramMotionEvent = null;
      switch (this.curveValue.activeType)
      {
      default: 
        label112:
        switch (this.activeSegment)
        {
        }
        break;
      }
      for (;;)
      {
        invalidate();
        if (this.delegate != null) {
          this.delegate.valueChanged();
        }
        this.lastX = f1;
        this.lastY = f2;
        break;
        paramMotionEvent = this.curveValue.luminanceCurve;
        break label112;
        paramMotionEvent = this.curveValue.redCurve;
        break label112;
        paramMotionEvent = this.curveValue.greenCurve;
        break label112;
        paramMotionEvent = this.curveValue.blueCurve;
        break label112;
        paramMotionEvent.blacksLevel = Math.max(0.0F, Math.min(100.0F, paramMotionEvent.blacksLevel + f3));
        continue;
        paramMotionEvent.shadowsLevel = Math.max(0.0F, Math.min(100.0F, paramMotionEvent.shadowsLevel + f3));
        continue;
        paramMotionEvent.midtonesLevel = Math.max(0.0F, Math.min(100.0F, paramMotionEvent.midtonesLevel + f3));
        continue;
        paramMotionEvent.highlightsLevel = Math.max(0.0F, Math.min(100.0F, paramMotionEvent.highlightsLevel + f3));
        continue;
        paramMotionEvent.whitesLevel = Math.max(0.0F, Math.min(100.0F, paramMotionEvent.whitesLevel + f3));
      }
      unselectSegments();
    }
  }
  
  private void selectSegmentWithPoint(float paramFloat)
  {
    if (this.activeSegment != 0) {}
    for (;;)
    {
      return;
      float f = this.actualArea.width / 5.0F;
      this.activeSegment = ((int)Math.floor((paramFloat - this.actualArea.x) / f + 1.0F));
    }
  }
  
  private void unselectSegments()
  {
    if (this.activeSegment == 0) {}
    for (;;)
    {
      return;
      this.activeSegment = 0;
    }
  }
  
  @SuppressLint({"DrawAllocation"})
  protected void onDraw(Canvas paramCanvas)
  {
    float f1 = this.actualArea.width / 5.0F;
    for (int i = 0; i < 4; i++)
    {
      f2 = this.actualArea.x;
      float f3 = i;
      f4 = this.actualArea.y;
      f5 = this.actualArea.x;
      float f6 = i;
      f7 = this.actualArea.y;
      paramCanvas.drawLine(f3 * f1 + (f2 + f1), f4, f6 * f1 + (f5 + f1), this.actualArea.height + f7, this.paint);
    }
    float f7 = this.actualArea.x;
    float f2 = this.actualArea.y;
    float f5 = this.actualArea.height;
    float f4 = this.actualArea.x;
    paramCanvas.drawLine(f7, f5 + f2, this.actualArea.width + f4, this.actualArea.y, this.paintDash);
    PhotoFilterView.CurvesValue localCurvesValue = null;
    switch (this.curveValue.activeType)
    {
    default: 
      i = 0;
      if (i < 5) {
        switch (i)
        {
        default: 
          localObject = "";
        }
      }
      break;
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      for (;;)
      {
        label218:
        f4 = this.textPaint.measureText((String)localObject);
        paramCanvas.drawText((String)localObject, this.actualArea.x + (f1 - f4) / 2.0F + i * f1, this.actualArea.y + this.actualArea.height - AndroidUtilities.dp(4.0F), this.textPaint);
        i++;
        break label218;
        this.paintCurve.setColor(-1);
        localCurvesValue = this.curveValue.luminanceCurve;
        break;
        this.paintCurve.setColor(-1229492);
        localCurvesValue = this.curveValue.redCurve;
        break;
        this.paintCurve.setColor(-15667555);
        localCurvesValue = this.curveValue.greenCurve;
        break;
        this.paintCurve.setColor(-13404165);
        localCurvesValue = this.curveValue.blueCurve;
        break;
        localObject = String.format(Locale.US, "%.2f", new Object[] { Float.valueOf(localCurvesValue.blacksLevel / 100.0F) });
        continue;
        localObject = String.format(Locale.US, "%.2f", new Object[] { Float.valueOf(localCurvesValue.shadowsLevel / 100.0F) });
        continue;
        localObject = String.format(Locale.US, "%.2f", new Object[] { Float.valueOf(localCurvesValue.midtonesLevel / 100.0F) });
        continue;
        localObject = String.format(Locale.US, "%.2f", new Object[] { Float.valueOf(localCurvesValue.highlightsLevel / 100.0F) });
        continue;
        localObject = String.format(Locale.US, "%.2f", new Object[] { Float.valueOf(localCurvesValue.whitesLevel / 100.0F) });
      }
    }
    Object localObject = localCurvesValue.interpolateCurve();
    invalidate();
    this.path.reset();
    i = 0;
    if (i < localObject.length / 2)
    {
      if (i == 0) {
        this.path.moveTo(this.actualArea.x + localObject[(i * 2)] * this.actualArea.width, this.actualArea.y + (1.0F - localObject[(i * 2 + 1)]) * this.actualArea.height);
      }
      for (;;)
      {
        i++;
        break;
        this.path.lineTo(this.actualArea.x + localObject[(i * 2)] * this.actualArea.width, this.actualArea.y + (1.0F - localObject[(i * 2 + 1)]) * this.actualArea.height);
      }
    }
    paramCanvas.drawPath(this.path, this.paintCurve);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    }
    for (;;)
    {
      return true;
      if (paramMotionEvent.getPointerCount() == 1)
      {
        if ((this.checkForMoving) && (!this.isMoving))
        {
          float f1 = paramMotionEvent.getX();
          float f2 = paramMotionEvent.getY();
          this.lastX = f1;
          this.lastY = f2;
          if ((f1 >= this.actualArea.x) && (f1 <= this.actualArea.x + this.actualArea.width) && (f2 >= this.actualArea.y) && (f2 <= this.actualArea.y + this.actualArea.height)) {
            this.isMoving = true;
          }
          this.checkForMoving = false;
          if (this.isMoving) {
            handlePan(1, paramMotionEvent);
          }
        }
      }
      else if (this.isMoving)
      {
        handlePan(3, paramMotionEvent);
        this.checkForMoving = true;
        this.isMoving = false;
        continue;
        if (this.isMoving)
        {
          handlePan(3, paramMotionEvent);
          this.isMoving = false;
        }
        this.checkForMoving = true;
        continue;
        if (this.isMoving) {
          handlePan(2, paramMotionEvent);
        }
      }
    }
  }
  
  public void setActualArea(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.actualArea.x = paramFloat1;
    this.actualArea.y = paramFloat2;
    this.actualArea.width = paramFloat3;
    this.actualArea.height = paramFloat4;
  }
  
  public void setDelegate(PhotoFilterCurvesControlDelegate paramPhotoFilterCurvesControlDelegate)
  {
    this.delegate = paramPhotoFilterCurvesControlDelegate;
  }
  
  public static abstract interface PhotoFilterCurvesControlDelegate
  {
    public abstract void valueChanged();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PhotoFilterCurvesControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */