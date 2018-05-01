package android.support.v4.widget;

import android.os.Build.VERSION;
import android.widget.EdgeEffect;

public final class EdgeEffectCompat
{
  private static final EdgeEffectBaseImpl IMPL;
  private EdgeEffect mEdgeEffect;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 21) {}
    for (IMPL = new EdgeEffectApi21Impl();; IMPL = new EdgeEffectBaseImpl()) {
      return;
    }
  }
  
  public static void onPull(EdgeEffect paramEdgeEffect, float paramFloat1, float paramFloat2)
  {
    IMPL.onPull(paramEdgeEffect, paramFloat1, paramFloat2);
  }
  
  static class EdgeEffectApi21Impl
    extends EdgeEffectCompat.EdgeEffectBaseImpl
  {
    public void onPull(EdgeEffect paramEdgeEffect, float paramFloat1, float paramFloat2)
    {
      paramEdgeEffect.onPull(paramFloat1, paramFloat2);
    }
  }
  
  static class EdgeEffectBaseImpl
  {
    public void onPull(EdgeEffect paramEdgeEffect, float paramFloat1, float paramFloat2)
    {
      paramEdgeEffect.onPull(paramFloat1);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/widget/EdgeEffectCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */