package org.telegram.ui.Components;

import android.graphics.PointF;
import android.view.animation.Interpolator;

public class CubicBezierInterpolator
  implements Interpolator
{
  public static final CubicBezierInterpolator DEFAULT = new CubicBezierInterpolator(0.25D, 0.1D, 0.25D, 1.0D);
  public static final CubicBezierInterpolator EASE_BOTH = new CubicBezierInterpolator(0.42D, 0.0D, 0.58D, 1.0D);
  public static final CubicBezierInterpolator EASE_IN;
  public static final CubicBezierInterpolator EASE_OUT = new CubicBezierInterpolator(0.0D, 0.0D, 0.58D, 1.0D);
  public static final CubicBezierInterpolator EASE_OUT_QUINT = new CubicBezierInterpolator(0.23D, 1.0D, 0.32D, 1.0D);
  protected PointF a = new PointF();
  protected PointF b = new PointF();
  protected PointF c = new PointF();
  protected PointF end;
  protected PointF start;
  
  static
  {
    EASE_IN = new CubicBezierInterpolator(0.42D, 0.0D, 1.0D, 1.0D);
  }
  
  public CubicBezierInterpolator(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    this((float)paramDouble1, (float)paramDouble2, (float)paramDouble3, (float)paramDouble4);
  }
  
  public CubicBezierInterpolator(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this(new PointF(paramFloat1, paramFloat2), new PointF(paramFloat3, paramFloat4));
  }
  
  public CubicBezierInterpolator(PointF paramPointF1, PointF paramPointF2)
    throws IllegalArgumentException
  {
    if ((paramPointF1.x < 0.0F) || (paramPointF1.x > 1.0F)) {
      throw new IllegalArgumentException("startX value must be in the range [0, 1]");
    }
    if ((paramPointF2.x < 0.0F) || (paramPointF2.x > 1.0F)) {
      throw new IllegalArgumentException("endX value must be in the range [0, 1]");
    }
    this.start = paramPointF1;
    this.end = paramPointF2;
  }
  
  private float getBezierCoordinateX(float paramFloat)
  {
    this.c.x = (this.start.x * 3.0F);
    this.b.x = ((this.end.x - this.start.x) * 3.0F - this.c.x);
    this.a.x = (1.0F - this.c.x - this.b.x);
    return (this.c.x + (this.b.x + this.a.x * paramFloat) * paramFloat) * paramFloat;
  }
  
  private float getXDerivate(float paramFloat)
  {
    return this.c.x + (2.0F * this.b.x + 3.0F * this.a.x * paramFloat) * paramFloat;
  }
  
  protected float getBezierCoordinateY(float paramFloat)
  {
    this.c.y = (this.start.y * 3.0F);
    this.b.y = ((this.end.y - this.start.y) * 3.0F - this.c.y);
    this.a.y = (1.0F - this.c.y - this.b.y);
    return (this.c.y + (this.b.y + this.a.y * paramFloat) * paramFloat) * paramFloat;
  }
  
  public float getInterpolation(float paramFloat)
  {
    return getBezierCoordinateY(getXForTime(paramFloat));
  }
  
  protected float getXForTime(float paramFloat)
  {
    float f1 = paramFloat;
    for (int i = 1;; i++)
    {
      float f2;
      if (i < 14)
      {
        f2 = getBezierCoordinateX(f1) - paramFloat;
        if (Math.abs(f2) >= 0.001D) {}
      }
      else
      {
        return f1;
      }
      f1 -= f2 / getXDerivate(f1);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/CubicBezierInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */