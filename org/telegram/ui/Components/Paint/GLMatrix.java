package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;

public class GLMatrix
{
  public static float[] LoadGraphicsMatrix(Matrix paramMatrix)
  {
    float[] arrayOfFloat = new float[9];
    paramMatrix.getValues(arrayOfFloat);
    return new float[] { arrayOfFloat[0], arrayOfFloat[1], 0.0F, 0.0F, arrayOfFloat[3], arrayOfFloat[4], 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, arrayOfFloat[2], arrayOfFloat[5], 0.0F, 1.0F };
  }
  
  public static float[] LoadOrtho(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    float f1 = -(paramFloat2 + paramFloat1) / (paramFloat2 - paramFloat1);
    float f2 = -(paramFloat4 + paramFloat3) / (paramFloat4 - paramFloat3);
    float f3 = -(paramFloat6 + paramFloat5) / (paramFloat6 - paramFloat5);
    return new float[] { 2.0F / (paramFloat2 - paramFloat1), 0.0F, 0.0F, 0.0F, 0.0F, 2.0F / (paramFloat4 - paramFloat3), 0.0F, 0.0F, 0.0F, 0.0F, -2.0F / (paramFloat6 - paramFloat5), 0.0F, f1, f2, f3, 1.0F };
  }
  
  public static float[] MultiplyMat4f(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    return new float[] { paramArrayOfFloat1[0] * paramArrayOfFloat2[0] + paramArrayOfFloat1[4] * paramArrayOfFloat2[1] + paramArrayOfFloat1[8] * paramArrayOfFloat2[2] + paramArrayOfFloat1[12] * paramArrayOfFloat2[3], paramArrayOfFloat1[1] * paramArrayOfFloat2[0] + paramArrayOfFloat1[5] * paramArrayOfFloat2[1] + paramArrayOfFloat1[9] * paramArrayOfFloat2[2] + paramArrayOfFloat1[13] * paramArrayOfFloat2[3], paramArrayOfFloat1[2] * paramArrayOfFloat2[0] + paramArrayOfFloat1[6] * paramArrayOfFloat2[1] + paramArrayOfFloat1[10] * paramArrayOfFloat2[2] + paramArrayOfFloat1[14] * paramArrayOfFloat2[3], paramArrayOfFloat1[3] * paramArrayOfFloat2[0] + paramArrayOfFloat1[7] * paramArrayOfFloat2[1] + paramArrayOfFloat1[11] * paramArrayOfFloat2[2] + paramArrayOfFloat1[15] * paramArrayOfFloat2[3], paramArrayOfFloat1[0] * paramArrayOfFloat2[4] + paramArrayOfFloat1[4] * paramArrayOfFloat2[5] + paramArrayOfFloat1[8] * paramArrayOfFloat2[6] + paramArrayOfFloat1[12] * paramArrayOfFloat2[7], paramArrayOfFloat1[1] * paramArrayOfFloat2[4] + paramArrayOfFloat1[5] * paramArrayOfFloat2[5] + paramArrayOfFloat1[9] * paramArrayOfFloat2[6] + paramArrayOfFloat1[13] * paramArrayOfFloat2[7], paramArrayOfFloat1[2] * paramArrayOfFloat2[4] + paramArrayOfFloat1[6] * paramArrayOfFloat2[5] + paramArrayOfFloat1[10] * paramArrayOfFloat2[6] + paramArrayOfFloat1[14] * paramArrayOfFloat2[7], paramArrayOfFloat1[3] * paramArrayOfFloat2[4] + paramArrayOfFloat1[7] * paramArrayOfFloat2[5] + paramArrayOfFloat1[11] * paramArrayOfFloat2[6] + paramArrayOfFloat1[15] * paramArrayOfFloat2[7], paramArrayOfFloat1[0] * paramArrayOfFloat2[8] + paramArrayOfFloat1[4] * paramArrayOfFloat2[9] + paramArrayOfFloat1[8] * paramArrayOfFloat2[10] + paramArrayOfFloat1[12] * paramArrayOfFloat2[11], paramArrayOfFloat1[1] * paramArrayOfFloat2[8] + paramArrayOfFloat1[5] * paramArrayOfFloat2[9] + paramArrayOfFloat1[9] * paramArrayOfFloat2[10] + paramArrayOfFloat1[13] * paramArrayOfFloat2[11], paramArrayOfFloat1[2] * paramArrayOfFloat2[8] + paramArrayOfFloat1[6] * paramArrayOfFloat2[9] + paramArrayOfFloat1[10] * paramArrayOfFloat2[10] + paramArrayOfFloat1[14] * paramArrayOfFloat2[11], paramArrayOfFloat1[3] * paramArrayOfFloat2[8] + paramArrayOfFloat1[7] * paramArrayOfFloat2[9] + paramArrayOfFloat1[11] * paramArrayOfFloat2[10] + paramArrayOfFloat1[15] * paramArrayOfFloat2[11], paramArrayOfFloat1[0] * paramArrayOfFloat2[12] + paramArrayOfFloat1[4] * paramArrayOfFloat2[13] + paramArrayOfFloat1[8] * paramArrayOfFloat2[14] + paramArrayOfFloat1[12] * paramArrayOfFloat2[15], paramArrayOfFloat1[1] * paramArrayOfFloat2[12] + paramArrayOfFloat1[5] * paramArrayOfFloat2[13] + paramArrayOfFloat1[9] * paramArrayOfFloat2[14] + paramArrayOfFloat1[13] * paramArrayOfFloat2[15], paramArrayOfFloat1[2] * paramArrayOfFloat2[12] + paramArrayOfFloat1[6] * paramArrayOfFloat2[13] + paramArrayOfFloat1[10] * paramArrayOfFloat2[14] + paramArrayOfFloat1[14] * paramArrayOfFloat2[15], paramArrayOfFloat1[3] * paramArrayOfFloat2[12] + paramArrayOfFloat1[7] * paramArrayOfFloat2[13] + paramArrayOfFloat1[11] * paramArrayOfFloat2[14] + paramArrayOfFloat1[15] * paramArrayOfFloat2[15] };
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/GLMatrix.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */