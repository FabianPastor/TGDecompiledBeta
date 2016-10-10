package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;
import android.view.MotionEvent;
import java.util.Vector;
import org.telegram.messenger.AndroidUtilities;

public class Input
{
  private boolean beganDrawing;
  private boolean clearBuffer;
  private boolean hasMoved;
  private Matrix invertMatrix;
  private boolean isFirst;
  private Point lastLocation;
  private double lastRemainder;
  private Point[] points = new Point[3];
  private int pointsCount;
  private RenderView renderView;
  private float[] tempPoint = new float[2];
  
  public Input(RenderView paramRenderView)
  {
    this.renderView = paramRenderView;
  }
  
  private void paintPath(final Path paramPath)
  {
    paramPath.setup(this.renderView.getCurrentColor(), this.renderView.getCurrentWeight(), this.renderView.getCurrentBrush());
    if (this.clearBuffer) {
      this.lastRemainder = 0.0D;
    }
    paramPath.remainder = this.lastRemainder;
    this.renderView.getPainting().paintStroke(paramPath, this.clearBuffer, new Runnable()
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            Input.access$002(Input.this, Input.1.this.val$path.remainder);
            Input.access$102(Input.this, false);
          }
        });
      }
    });
  }
  
  private void reset()
  {
    this.pointsCount = 0;
  }
  
  private Point smoothPoint(Point paramPoint1, Point paramPoint2, Point paramPoint3, float paramFloat)
  {
    double d1 = Math.pow(1.0F - paramFloat, 2.0D);
    double d2 = 2.0F * (1.0F - paramFloat) * paramFloat;
    double d3 = paramFloat * paramFloat;
    return new Point(paramPoint1.x * d1 + paramPoint3.x * d2 + paramPoint2.x * d3, paramPoint1.y * d1 + paramPoint3.y * d2 + paramPoint2.y * d3, 1.0D);
  }
  
  private void smoothenAndPaintPoints(boolean paramBoolean)
  {
    if (this.pointsCount > 2)
    {
      localObject1 = new Vector();
      Point localPoint1 = this.points[0];
      Object localObject2 = this.points[1];
      Point localPoint2 = this.points[2];
      if ((localPoint2 == null) || (localObject2 == null) || (localPoint1 == null)) {
        return;
      }
      localPoint1 = ((Point)localObject2).multiplySum(localPoint1, 0.5D);
      localPoint2 = localPoint2.multiplySum((Point)localObject2, 0.5D);
      int j = (int)Math.min(48.0D, Math.max(Math.floor(localPoint1.getDistanceTo(localPoint2) / 1), 24.0D));
      float f1 = 0.0F;
      float f2 = 1.0F / j;
      int i = 0;
      while (i < j)
      {
        Point localPoint3 = smoothPoint(localPoint1, localPoint2, (Point)localObject2, f1);
        if (this.isFirst)
        {
          localPoint3.edge = true;
          this.isFirst = false;
        }
        ((Vector)localObject1).add(localPoint3);
        f1 += f2;
        i += 1;
      }
      if (paramBoolean) {
        localPoint2.edge = true;
      }
      ((Vector)localObject1).add(localPoint2);
      localObject2 = new Point[((Vector)localObject1).size()];
      ((Vector)localObject1).toArray((Object[])localObject2);
      paintPath(new Path((Point[])localObject2));
      System.arraycopy(this.points, 1, this.points, 0, 2);
      if (paramBoolean)
      {
        this.pointsCount = 0;
        return;
      }
      this.pointsCount = 2;
      return;
    }
    Object localObject1 = new Point[this.pointsCount];
    System.arraycopy(this.points, 0, localObject1, 0, this.pointsCount);
    paintPath(new Path((Point[])localObject1));
  }
  
  public void process(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    float f1 = paramMotionEvent.getX();
    float f2 = this.renderView.getHeight();
    float f3 = paramMotionEvent.getY();
    this.tempPoint[0] = f1;
    this.tempPoint[1] = (f2 - f3);
    this.invertMatrix.mapPoints(this.tempPoint);
    paramMotionEvent = new Point(this.tempPoint[0], this.tempPoint[1], 1.0D);
    switch (i)
    {
    default: 
    case 0: 
    case 2: 
      do
      {
        return;
        if (!this.beganDrawing)
        {
          this.beganDrawing = true;
          this.hasMoved = false;
          this.isFirst = true;
          this.lastLocation = paramMotionEvent;
          this.points[0] = paramMotionEvent;
          this.pointsCount = 1;
          this.clearBuffer = true;
          return;
        }
      } while (paramMotionEvent.getDistanceTo(this.lastLocation) < AndroidUtilities.dp(5.0F));
      if (!this.hasMoved)
      {
        this.renderView.onBeganDrawing();
        this.hasMoved = true;
      }
      this.points[this.pointsCount] = paramMotionEvent;
      this.pointsCount += 1;
      if (this.pointsCount == 3) {
        smoothenAndPaintPoints(false);
      }
      this.lastLocation = paramMotionEvent;
      return;
    }
    if (!this.hasMoved)
    {
      if (this.renderView.shouldDraw())
      {
        paramMotionEvent.edge = true;
        paintPath(new Path(paramMotionEvent));
      }
      reset();
    }
    for (;;)
    {
      this.pointsCount = 0;
      this.renderView.getPainting().commitStroke(this.renderView.getCurrentColor());
      this.beganDrawing = false;
      this.renderView.onFinishedDrawing(this.hasMoved);
      return;
      if (this.pointsCount > 0) {
        smoothenAndPaintPoints(true);
      }
    }
  }
  
  public void setMatrix(Matrix paramMatrix)
  {
    this.invertMatrix = new Matrix();
    paramMatrix.invert(this.invertMatrix);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Input.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */