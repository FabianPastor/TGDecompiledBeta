package com.google.android.gms.internal;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class zzbkc
{
  public static Bitmap zzb(Bitmap paramBitmap, zzbka paramzzbka)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    Object localObject = paramBitmap;
    if (paramzzbka.rotation != 0)
    {
      localObject = new Matrix();
      ((Matrix)localObject).postRotate(zzom(paramzzbka.rotation));
      localObject = Bitmap.createBitmap(paramBitmap, 0, 0, i, j, (Matrix)localObject, false);
    }
    if ((paramzzbka.rotation == 1) || (paramzzbka.rotation == 3))
    {
      paramzzbka.width = j;
      paramzzbka.height = i;
    }
    return (Bitmap)localObject;
  }
  
  private static int zzom(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unsupported rotation degree.");
    case 0: 
      return 0;
    case 1: 
      return 90;
    case 2: 
      return 180;
    }
    return 270;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbkc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */