package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.vision.text.internal.client.BoundingBoxParcel;

final class zza
{
  static Rect zza(Text paramText)
  {
    int k = Integer.MAX_VALUE;
    int j = Integer.MIN_VALUE;
    paramText = paramText.getCornerPoints();
    int i1 = paramText.length;
    int i = 0;
    int m = Integer.MIN_VALUE;
    int n = Integer.MAX_VALUE;
    while (i < i1)
    {
      Object localObject = paramText[i];
      n = Math.min(n, ((Point)localObject).x);
      m = Math.max(m, ((Point)localObject).x);
      k = Math.min(k, ((Point)localObject).y);
      j = Math.max(j, ((Point)localObject).y);
      i += 1;
    }
    return new Rect(n, k, m, j);
  }
  
  static Point[] zza(BoundingBoxParcel paramBoundingBoxParcel)
  {
    Point[] arrayOfPoint = new Point[4];
    double d2 = Math.sin(Math.toRadians(paramBoundingBoxParcel.aLE));
    double d1 = Math.cos(Math.toRadians(paramBoundingBoxParcel.aLE));
    arrayOfPoint[0] = new Point(paramBoundingBoxParcel.left, paramBoundingBoxParcel.top);
    arrayOfPoint[1] = new Point((int)(paramBoundingBoxParcel.left + paramBoundingBoxParcel.width * d1), (int)(paramBoundingBoxParcel.top + paramBoundingBoxParcel.width * d2));
    int i = (int)(arrayOfPoint[1].x - d2 * paramBoundingBoxParcel.height);
    d2 = arrayOfPoint[1].y;
    arrayOfPoint[2] = new Point(i, (int)(d1 * paramBoundingBoxParcel.height + d2));
    arrayOfPoint[3] = new Point(arrayOfPoint[0].x + (arrayOfPoint[2].x - arrayOfPoint[1].x), arrayOfPoint[0].y + (arrayOfPoint[2].y - arrayOfPoint[1].y));
    return arrayOfPoint;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */