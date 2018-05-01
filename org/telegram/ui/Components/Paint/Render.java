package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Render
{
  private static RectF Draw(RenderState paramRenderState)
  {
    RectF localRectF = new RectF(0.0F, 0.0F, 0.0F, 0.0F);
    int i = paramRenderState.getCount();
    if (i == 0) {}
    for (;;)
    {
      return localRectF;
      Object localObject = ByteBuffer.allocateDirect(20 * (i * 4 + (i - 1) * 2));
      ((ByteBuffer)localObject).order(ByteOrder.nativeOrder());
      FloatBuffer localFloatBuffer = ((ByteBuffer)localObject).asFloatBuffer();
      localFloatBuffer.position(0);
      paramRenderState.setPosition(0);
      int j = 0;
      for (int k = 0; k < i; k++)
      {
        float f1 = paramRenderState.read();
        float f2 = paramRenderState.read();
        float f3 = paramRenderState.read();
        float f4 = paramRenderState.read();
        float f5 = paramRenderState.read();
        localObject = new RectF(f1 - f3, f2 - f3, f1 + f3, f2 + f3);
        float[] arrayOfFloat = new float[8];
        arrayOfFloat[0] = ((RectF)localObject).left;
        arrayOfFloat[1] = ((RectF)localObject).top;
        arrayOfFloat[2] = ((RectF)localObject).right;
        arrayOfFloat[3] = ((RectF)localObject).top;
        arrayOfFloat[4] = ((RectF)localObject).left;
        arrayOfFloat[5] = ((RectF)localObject).bottom;
        arrayOfFloat[6] = ((RectF)localObject).right;
        arrayOfFloat[7] = ((RectF)localObject).bottom;
        f3 = ((RectF)localObject).centerX();
        f2 = ((RectF)localObject).centerY();
        Matrix localMatrix = new Matrix();
        localMatrix.setRotate((float)Math.toDegrees(f4), f3, f2);
        localMatrix.mapPoints(arrayOfFloat);
        localMatrix.mapRect((RectF)localObject);
        Utils.RectFIntegral((RectF)localObject);
        localRectF.union((RectF)localObject);
        int m = j;
        if (j != 0)
        {
          localFloatBuffer.put(arrayOfFloat[0]);
          localFloatBuffer.put(arrayOfFloat[1]);
          localFloatBuffer.put(0.0F);
          localFloatBuffer.put(0.0F);
          localFloatBuffer.put(f5);
          m = j + 1;
        }
        localFloatBuffer.put(arrayOfFloat[0]);
        localFloatBuffer.put(arrayOfFloat[1]);
        localFloatBuffer.put(0.0F);
        localFloatBuffer.put(0.0F);
        localFloatBuffer.put(f5);
        localFloatBuffer.put(arrayOfFloat[2]);
        localFloatBuffer.put(arrayOfFloat[3]);
        localFloatBuffer.put(1.0F);
        localFloatBuffer.put(0.0F);
        localFloatBuffer.put(f5);
        localFloatBuffer.put(arrayOfFloat[4]);
        localFloatBuffer.put(arrayOfFloat[5]);
        localFloatBuffer.put(0.0F);
        localFloatBuffer.put(1.0F);
        localFloatBuffer.put(f5);
        localFloatBuffer.put(arrayOfFloat[6]);
        localFloatBuffer.put(arrayOfFloat[7]);
        localFloatBuffer.put(1.0F);
        localFloatBuffer.put(1.0F);
        localFloatBuffer.put(f5);
        m = m + 1 + 1 + 1 + 1;
        j = m;
        if (k != i - 1)
        {
          localFloatBuffer.put(arrayOfFloat[6]);
          localFloatBuffer.put(arrayOfFloat[7]);
          localFloatBuffer.put(1.0F);
          localFloatBuffer.put(1.0F);
          localFloatBuffer.put(f5);
          j = m + 1;
        }
      }
      localFloatBuffer.position(0);
      GLES20.glVertexAttribPointer(0, 2, 5126, false, 20, localFloatBuffer.slice());
      GLES20.glEnableVertexAttribArray(0);
      localFloatBuffer.position(2);
      GLES20.glVertexAttribPointer(1, 2, 5126, true, 20, localFloatBuffer.slice());
      GLES20.glEnableVertexAttribArray(1);
      localFloatBuffer.position(4);
      GLES20.glVertexAttribPointer(2, 1, 5126, true, 20, localFloatBuffer.slice());
      GLES20.glEnableVertexAttribArray(2);
      GLES20.glDrawArrays(5, 0, j);
    }
  }
  
  private static void PaintSegment(Point paramPoint1, Point paramPoint2, RenderState paramRenderState)
  {
    double d1 = paramPoint1.getDistanceTo(paramPoint2);
    Point localPoint1 = paramPoint2.substract(paramPoint1);
    Point localPoint2 = new Point(1.0D, 1.0D, 0.0D);
    float f1;
    float f2;
    double d2;
    float f3;
    boolean bool1;
    boolean bool2;
    boolean bool3;
    if (Math.abs(paramRenderState.angle) > 0.0F)
    {
      f1 = paramRenderState.angle;
      f2 = paramRenderState.baseWeight * paramRenderState.scale;
      d2 = Math.max(1.0F, paramRenderState.spacing * f2);
      if (d1 > 0.0D) {
        localPoint2 = localPoint1.multiplyByScalar(1.0D / d1);
      }
      f3 = Math.min(1.0F, paramRenderState.alpha * 1.15F);
      bool1 = paramPoint1.edge;
      bool2 = paramPoint2.edge;
      int i = (int)Math.ceil((d1 - paramRenderState.remainder) / d2);
      int j = paramRenderState.getCount();
      paramRenderState.appendValuesCount(i);
      paramRenderState.setPosition(j);
      paramPoint1 = paramPoint1.add(localPoint2.multiplyByScalar(paramRenderState.remainder));
      bool3 = true;
    }
    for (double d3 = paramRenderState.remainder;; d3 += d2)
    {
      if (d3 <= d1) {
        if (!bool1) {
          break label263;
        }
      }
      label263:
      for (float f4 = f3;; f4 = paramRenderState.alpha)
      {
        bool3 = paramRenderState.addPoint(paramPoint1.toPointF(), f2, f1, f4, -1);
        if (bool3) {
          break label272;
        }
        if ((bool3) && (bool2))
        {
          paramRenderState.appendValuesCount(1);
          paramRenderState.addPoint(paramPoint2.toPointF(), f2, f1, f3, -1);
        }
        paramRenderState.remainder = (d3 - d1);
        return;
        f1 = (float)Math.atan2(localPoint1.y, localPoint1.x);
        break;
      }
      label272:
      paramPoint1 = paramPoint1.add(localPoint2.multiplyByScalar(d2));
      bool1 = false;
    }
  }
  
  private static void PaintStamp(Point paramPoint, RenderState paramRenderState)
  {
    float f1 = 0.0F;
    float f2 = paramRenderState.baseWeight;
    float f3 = paramRenderState.scale;
    paramPoint = paramPoint.toPointF();
    if (Math.abs(paramRenderState.angle) > 0.0F) {
      f1 = paramRenderState.angle;
    }
    float f4 = paramRenderState.alpha;
    paramRenderState.prepare();
    paramRenderState.appendValuesCount(1);
    paramRenderState.addPoint(paramPoint, f2 * f3, f1, f4, 0);
  }
  
  public static RectF RenderPath(Path paramPath, RenderState paramRenderState)
  {
    paramRenderState.baseWeight = paramPath.getBaseWeight();
    paramRenderState.spacing = paramPath.getBrush().getSpacing();
    paramRenderState.alpha = paramPath.getBrush().getAlpha();
    paramRenderState.angle = paramPath.getBrush().getAngle();
    paramRenderState.scale = paramPath.getBrush().getScale();
    int i = paramPath.getLength();
    if (i == 0)
    {
      paramPath = null;
      return paramPath;
    }
    if (i == 1) {
      PaintStamp(paramPath.getPoints()[0], paramRenderState);
    }
    for (;;)
    {
      paramPath.remainder = paramRenderState.remainder;
      paramPath = Draw(paramRenderState);
      break;
      Point[] arrayOfPoint = paramPath.getPoints();
      paramRenderState.prepare();
      for (i = 0; i < arrayOfPoint.length - 1; i++) {
        PaintSegment(arrayOfPoint[i], arrayOfPoint[(i + 1)], paramRenderState);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Render.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */