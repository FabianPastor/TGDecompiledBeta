package org.telegram.ui.Components.Paint;

import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class Utils
{
  public static void HasGLError()
  {
    int i = GLES20.glGetError();
    if (i != 0) {
      Log.d("Paint", GLUtils.getEGLErrorString(i));
    }
  }
  
  public static void RectFIntegral(RectF paramRectF)
  {
    paramRectF.left = ((int)Math.floor(paramRectF.left));
    paramRectF.top = ((int)Math.floor(paramRectF.top));
    paramRectF.right = ((int)Math.ceil(paramRectF.right));
    paramRectF.bottom = ((int)Math.ceil(paramRectF.bottom));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */