package android.support.v4.widget;

import android.widget.EdgeEffect;

class EdgeEffectCompatLollipop
{
  public static boolean onPull(Object paramObject, float paramFloat1, float paramFloat2)
  {
    ((EdgeEffect)paramObject).onPull(paramFloat1, paramFloat2);
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/widget/EdgeEffectCompatLollipop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */