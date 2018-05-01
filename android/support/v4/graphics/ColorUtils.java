package android.support.v4.graphics;

import android.graphics.Color;

public final class ColorUtils
{
  private static final ThreadLocal<double[]> TEMP_ARRAY = new ThreadLocal();
  
  public static void RGBToHSL(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat)
  {
    float f1 = paramInt1 / 255.0F;
    float f2 = paramInt2 / 255.0F;
    float f3 = paramInt3 / 255.0F;
    float f4 = Math.max(f1, Math.max(f2, f3));
    float f5 = Math.min(f1, Math.min(f2, f3));
    float f6 = f4 - f5;
    float f7 = (f4 + f5) / 2.0F;
    if (f4 == f5)
    {
      f4 = 0.0F;
      f6 = 0.0F;
      f1 = 60.0F * f6 % 360.0F;
      f6 = f1;
      if (f1 < 0.0F) {
        f6 = f1 + 360.0F;
      }
      paramArrayOfFloat[0] = constrain(f6, 0.0F, 360.0F);
      paramArrayOfFloat[1] = constrain(f4, 0.0F, 1.0F);
      paramArrayOfFloat[2] = constrain(f7, 0.0F, 1.0F);
      return;
    }
    if (f4 == f1) {
      f4 = (f2 - f3) / f6 % 6.0F;
    }
    for (;;)
    {
      f1 = f6 / (1.0F - Math.abs(2.0F * f7 - 1.0F));
      f6 = f4;
      f4 = f1;
      break;
      if (f4 == f2) {
        f4 = (f3 - f1) / f6 + 2.0F;
      } else {
        f4 = (f1 - f2) / f6 + 4.0F;
      }
    }
  }
  
  public static void RGBToXYZ(int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble)
  {
    if (paramArrayOfDouble.length != 3) {
      throw new IllegalArgumentException("outXyz must have a length of 3.");
    }
    double d1 = paramInt1 / 255.0D;
    double d2;
    label66:
    double d3;
    if (d1 < 0.04045D)
    {
      d1 /= 12.92D;
      d2 = paramInt2 / 255.0D;
      if (d2 >= 0.04045D) {
        break label194;
      }
      d2 /= 12.92D;
      d3 = paramInt3 / 255.0D;
      if (d3 >= 0.04045D) {
        break label215;
      }
    }
    label194:
    label215:
    for (d3 /= 12.92D;; d3 = Math.pow((0.055D + d3) / 1.055D, 2.4D))
    {
      paramArrayOfDouble[0] = (100.0D * (0.4124D * d1 + 0.3576D * d2 + 0.1805D * d3));
      paramArrayOfDouble[1] = (100.0D * (0.2126D * d1 + 0.7152D * d2 + 0.0722D * d3));
      paramArrayOfDouble[2] = (100.0D * (0.0193D * d1 + 0.1192D * d2 + 0.9505D * d3));
      return;
      d1 = Math.pow((0.055D + d1) / 1.055D, 2.4D);
      break;
      d2 = Math.pow((0.055D + d2) / 1.055D, 2.4D);
      break label66;
    }
  }
  
  public static double calculateContrast(int paramInt1, int paramInt2)
  {
    if (Color.alpha(paramInt2) != 255) {
      throw new IllegalArgumentException("background can not be translucent: #" + Integer.toHexString(paramInt2));
    }
    int i = paramInt1;
    if (Color.alpha(paramInt1) < 255) {
      i = compositeColors(paramInt1, paramInt2);
    }
    double d1 = calculateLuminance(i) + 0.05D;
    double d2 = calculateLuminance(paramInt2) + 0.05D;
    return Math.max(d1, d2) / Math.min(d1, d2);
  }
  
  public static double calculateLuminance(int paramInt)
  {
    double[] arrayOfDouble = getTempDouble3Array();
    colorToXYZ(paramInt, arrayOfDouble);
    return arrayOfDouble[1] / 100.0D;
  }
  
  public static int calculateMinimumAlpha(int paramInt1, int paramInt2, float paramFloat)
  {
    if (Color.alpha(paramInt2) != 255) {
      throw new IllegalArgumentException("background can not be translucent: #" + Integer.toHexString(paramInt2));
    }
    if (calculateContrast(setAlphaComponent(paramInt1, 255), paramInt2) < paramFloat) {
      i = -1;
    }
    int j;
    int k;
    int m;
    do
    {
      do
      {
        return i;
        j = 0;
        k = 0;
        m = 255;
        i = m;
      } while (j > 10);
      i = m;
    } while (m - k <= 1);
    int i = (k + m) / 2;
    if (calculateContrast(setAlphaComponent(paramInt1, i), paramInt2) < paramFloat) {
      k = i;
    }
    for (;;)
    {
      j++;
      break;
      m = i;
    }
  }
  
  public static void colorToHSL(int paramInt, float[] paramArrayOfFloat)
  {
    RGBToHSL(Color.red(paramInt), Color.green(paramInt), Color.blue(paramInt), paramArrayOfFloat);
  }
  
  public static void colorToXYZ(int paramInt, double[] paramArrayOfDouble)
  {
    RGBToXYZ(Color.red(paramInt), Color.green(paramInt), Color.blue(paramInt), paramArrayOfDouble);
  }
  
  private static int compositeAlpha(int paramInt1, int paramInt2)
  {
    return 255 - (255 - paramInt2) * (255 - paramInt1) / 255;
  }
  
  public static int compositeColors(int paramInt1, int paramInt2)
  {
    int i = Color.alpha(paramInt2);
    int j = Color.alpha(paramInt1);
    int k = compositeAlpha(j, i);
    return Color.argb(k, compositeComponent(Color.red(paramInt1), j, Color.red(paramInt2), i, k), compositeComponent(Color.green(paramInt1), j, Color.green(paramInt2), i, k), compositeComponent(Color.blue(paramInt1), j, Color.blue(paramInt2), i, k));
  }
  
  private static int compositeComponent(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramInt5 == 0) {}
    for (paramInt1 = 0;; paramInt1 = (paramInt1 * 255 * paramInt2 + paramInt3 * paramInt4 * (255 - paramInt2)) / (paramInt5 * 255)) {
      return paramInt1;
    }
  }
  
  private static float constrain(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 < paramFloat2) {}
    for (;;)
    {
      return paramFloat2;
      if (paramFloat1 > paramFloat3) {
        paramFloat2 = paramFloat3;
      } else {
        paramFloat2 = paramFloat1;
      }
    }
  }
  
  private static double[] getTempDouble3Array()
  {
    double[] arrayOfDouble1 = (double[])TEMP_ARRAY.get();
    double[] arrayOfDouble2 = arrayOfDouble1;
    if (arrayOfDouble1 == null)
    {
      arrayOfDouble2 = new double[3];
      TEMP_ARRAY.set(arrayOfDouble2);
    }
    return arrayOfDouble2;
  }
  
  public static int setAlphaComponent(int paramInt1, int paramInt2)
  {
    if ((paramInt2 < 0) || (paramInt2 > 255)) {
      throw new IllegalArgumentException("alpha must be between 0 and 255.");
    }
    return 0xFFFFFF & paramInt1 | paramInt2 << 24;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/graphics/ColorUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */