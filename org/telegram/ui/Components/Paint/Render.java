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
    RectF localRectF1 = new RectF(0.0F, 0.0F, 0.0F, 0.0F);
    int m = paramRenderState.getCount();
    if (m == 0) {
      return localRectF1;
    }
    Object localObject = ByteBuffer.allocateDirect(20 * (m * 4 + (m - 1) * 2));
    ((ByteBuffer)localObject).order(ByteOrder.nativeOrder());
    localObject = ((ByteBuffer)localObject).asFloatBuffer();
    ((FloatBuffer)localObject).position(0);
    paramRenderState.setPosition(0);
    int i = 0;
    int j = 0;
    while (j < m)
    {
      float f3 = paramRenderState.read();
      float f4 = paramRenderState.read();
      float f5 = paramRenderState.read();
      float f1 = paramRenderState.read();
      float f2 = paramRenderState.read();
      RectF localRectF2 = new RectF(f3 - f5, f4 - f5, f3 + f5, f4 + f5);
      float[] arrayOfFloat = new float[8];
      arrayOfFloat[0] = localRectF2.left;
      arrayOfFloat[1] = localRectF2.top;
      arrayOfFloat[2] = localRectF2.right;
      arrayOfFloat[3] = localRectF2.top;
      arrayOfFloat[4] = localRectF2.left;
      arrayOfFloat[5] = localRectF2.bottom;
      arrayOfFloat[6] = localRectF2.right;
      arrayOfFloat[7] = localRectF2.bottom;
      f3 = localRectF2.centerX();
      f4 = localRectF2.centerY();
      Matrix localMatrix = new Matrix();
      localMatrix.setRotate((float)Math.toDegrees(f1), f3, f4);
      localMatrix.mapPoints(arrayOfFloat);
      localMatrix.mapRect(localRectF2);
      Utils.RectFIntegral(localRectF2);
      localRectF1.union(localRectF2);
      int k = i;
      if (i != 0)
      {
        ((FloatBuffer)localObject).put(arrayOfFloat[0]);
        ((FloatBuffer)localObject).put(arrayOfFloat[1]);
        ((FloatBuffer)localObject).put(0.0F);
        ((FloatBuffer)localObject).put(0.0F);
        ((FloatBuffer)localObject).put(f2);
        k = i + 1;
      }
      ((FloatBuffer)localObject).put(arrayOfFloat[0]);
      ((FloatBuffer)localObject).put(arrayOfFloat[1]);
      ((FloatBuffer)localObject).put(0.0F);
      ((FloatBuffer)localObject).put(0.0F);
      ((FloatBuffer)localObject).put(f2);
      ((FloatBuffer)localObject).put(arrayOfFloat[2]);
      ((FloatBuffer)localObject).put(arrayOfFloat[3]);
      ((FloatBuffer)localObject).put(1.0F);
      ((FloatBuffer)localObject).put(0.0F);
      ((FloatBuffer)localObject).put(f2);
      ((FloatBuffer)localObject).put(arrayOfFloat[4]);
      ((FloatBuffer)localObject).put(arrayOfFloat[5]);
      ((FloatBuffer)localObject).put(0.0F);
      ((FloatBuffer)localObject).put(1.0F);
      ((FloatBuffer)localObject).put(f2);
      ((FloatBuffer)localObject).put(arrayOfFloat[6]);
      ((FloatBuffer)localObject).put(arrayOfFloat[7]);
      ((FloatBuffer)localObject).put(1.0F);
      ((FloatBuffer)localObject).put(1.0F);
      ((FloatBuffer)localObject).put(f2);
      k = k + 1 + 1 + 1 + 1;
      i = k;
      if (j != m - 1)
      {
        ((FloatBuffer)localObject).put(arrayOfFloat[6]);
        ((FloatBuffer)localObject).put(arrayOfFloat[7]);
        ((FloatBuffer)localObject).put(1.0F);
        ((FloatBuffer)localObject).put(1.0F);
        ((FloatBuffer)localObject).put(f2);
        i = k + 1;
      }
      j += 1;
    }
    ((FloatBuffer)localObject).position(0);
    GLES20.glVertexAttribPointer(0, 2, 5126, false, 20, ((FloatBuffer)localObject).slice());
    GLES20.glEnableVertexAttribArray(0);
    ((FloatBuffer)localObject).position(2);
    GLES20.glVertexAttribPointer(1, 2, 5126, true, 20, ((FloatBuffer)localObject).slice());
    GLES20.glEnableVertexAttribArray(1);
    ((FloatBuffer)localObject).position(4);
    GLES20.glVertexAttribPointer(2, 1, 5126, true, 20, ((FloatBuffer)localObject).slice());
    GLES20.glEnableVertexAttribArray(2);
    GLES20.glDrawArrays(5, 0, i);
    return localRectF1;
  }
  
  private static void PaintSegment(Point paramPoint1, Point paramPoint2, RenderState paramRenderState)
  {
    double d2 = paramPoint1.getDistanceTo(paramPoint2);
    Point localPoint2 = paramPoint2.substract(paramPoint1);
    Point localPoint1 = new Point(1.0D, 1.0D, 0.0D);
    float f1;
    float f4;
    double d3;
    float f3;
    boolean bool2;
    boolean bool3;
    boolean bool1;
    if (Math.abs(paramRenderState.angle) > 0.0F)
    {
      f1 = paramRenderState.angle;
      f4 = paramRenderState.baseWeight * paramRenderState.scale;
      d3 = Math.max(1.0F, paramRenderState.spacing * f4);
      if (d2 > 0.0D) {
        localPoint1 = localPoint2.multiplyByScalar(1.0D / d2);
      }
      f3 = Math.min(1.0F, paramRenderState.alpha * 1.15F);
      bool2 = paramPoint1.edge;
      bool3 = paramPoint2.edge;
      int i = (int)Math.ceil((d2 - paramRenderState.remainder) / d3);
      int j = paramRenderState.getCount();
      paramRenderState.appendValuesCount(i);
      paramRenderState.setPosition(j);
      paramPoint1 = paramPoint1.add(localPoint1.multiplyByScalar(paramRenderState.remainder));
      bool1 = true;
    }
    for (double d1 = paramRenderState.remainder;; d1 += d3)
    {
      if (d1 <= d2) {
        if (!bool2) {
          break label266;
        }
      }
      label266:
      for (float f2 = f3;; f2 = paramRenderState.alpha)
      {
        bool1 = paramRenderState.addPoint(paramPoint1.toPointF(), f4, f1, f2, -1);
        if (bool1) {
          break label275;
        }
        if ((bool1) && (bool3))
        {
          paramRenderState.appendValuesCount(1);
          paramRenderState.addPoint(paramPoint2.toPointF(), f4, f1, f3, -1);
        }
        paramRenderState.remainder = (d1 - d2);
        return;
        f1 = (float)Math.atan2(localPoint2.y, localPoint2.x);
        break;
      }
      label275:
      paramPoint1 = paramPoint1.add(localPoint1.multiplyByScalar(d3));
      bool2 = false;
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
    if (i == 0) {
      return null;
    }
    if (i == 1) {
      PaintStamp(paramPath.getPoints()[0], paramRenderState);
    }
    for (;;)
    {
      paramPath.remainder = paramRenderState.remainder;
      return Draw(paramRenderState);
      Point[] arrayOfPoint = paramPath.getPoints();
      paramRenderState.prepare();
      i = 0;
      while (i < arrayOfPoint.length - 1)
      {
        PaintSegment(arrayOfPoint[i], arrayOfPoint[(i + 1)], paramRenderState);
        i += 1;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Render.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */