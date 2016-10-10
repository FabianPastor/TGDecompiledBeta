package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.SparseArray;
import com.google.android.gms.vision.text.internal.client.BoundingBoxParcel;
import com.google.android.gms.vision.text.internal.client.LineBoxParcel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class TextBlock
  implements Text
{
  private Rect aLA;
  private LineBoxParcel[] aLx;
  private List<Line> aLy;
  private String aLz;
  private Point[] cornerPoints;
  
  TextBlock(SparseArray<LineBoxParcel> paramSparseArray)
  {
    this.aLx = new LineBoxParcel[paramSparseArray.size()];
    int i = 0;
    while (i < this.aLx.length)
    {
      this.aLx[i] = ((LineBoxParcel)paramSparseArray.valueAt(i));
      i += 1;
    }
  }
  
  private static Point[] zza(int paramInt1, int paramInt2, int paramInt3, int paramInt4, BoundingBoxParcel paramBoundingBoxParcel)
  {
    int i = paramBoundingBoxParcel.left;
    int j = paramBoundingBoxParcel.top;
    double d1 = Math.sin(Math.toRadians(paramBoundingBoxParcel.aLE));
    double d2 = Math.cos(Math.toRadians(paramBoundingBoxParcel.aLE));
    paramBoundingBoxParcel = new Point[4];
    paramBoundingBoxParcel[0] = new Point(paramInt1, paramInt2);
    paramBoundingBoxParcel[1] = new Point(paramInt3, paramInt2);
    paramBoundingBoxParcel[2] = new Point(paramInt3, paramInt4);
    paramBoundingBoxParcel[3] = new Point(paramInt1, paramInt4);
    paramInt1 = 0;
    while (paramInt1 < 4)
    {
      paramInt2 = (int)(paramBoundingBoxParcel[paramInt1].x * d2 - paramBoundingBoxParcel[paramInt1].y * d1);
      paramInt3 = (int)(paramBoundingBoxParcel[paramInt1].x * d1 + paramBoundingBoxParcel[paramInt1].y * d2);
      paramBoundingBoxParcel[paramInt1].x = paramInt2;
      paramBoundingBoxParcel[paramInt1].y = paramInt3;
      paramBoundingBoxParcel[paramInt1].offset(i, j);
      paramInt1 += 1;
    }
    return paramBoundingBoxParcel;
  }
  
  private static Point[] zza(BoundingBoxParcel paramBoundingBoxParcel1, BoundingBoxParcel paramBoundingBoxParcel2)
  {
    int i = -paramBoundingBoxParcel2.left;
    int j = -paramBoundingBoxParcel2.top;
    double d1 = Math.sin(Math.toRadians(paramBoundingBoxParcel2.aLE));
    double d2 = Math.cos(Math.toRadians(paramBoundingBoxParcel2.aLE));
    paramBoundingBoxParcel2 = new Point[4];
    paramBoundingBoxParcel2[0] = new Point(paramBoundingBoxParcel1.left, paramBoundingBoxParcel1.top);
    paramBoundingBoxParcel2[0].offset(i, j);
    i = (int)(paramBoundingBoxParcel2[0].x * d2 + paramBoundingBoxParcel2[0].y * d1);
    j = (int)(d1 * -paramBoundingBoxParcel2[0].x + d2 * paramBoundingBoxParcel2[0].y);
    paramBoundingBoxParcel2[0].x = i;
    paramBoundingBoxParcel2[0].y = j;
    paramBoundingBoxParcel2[1] = new Point(paramBoundingBoxParcel1.width + i, j);
    paramBoundingBoxParcel2[2] = new Point(paramBoundingBoxParcel1.width + i, paramBoundingBoxParcel1.height + j);
    paramBoundingBoxParcel2[3] = new Point(i, j + paramBoundingBoxParcel1.height);
    return paramBoundingBoxParcel2;
  }
  
  public Rect getBoundingBox()
  {
    if (this.aLA == null) {
      this.aLA = zza.zza(this);
    }
    return this.aLA;
  }
  
  public List<? extends Text> getComponents()
  {
    return zzclw();
  }
  
  public Point[] getCornerPoints()
  {
    if (this.cornerPoints == null) {
      zzclv();
    }
    return this.cornerPoints;
  }
  
  public String getLanguage()
  {
    if (this.aLz != null) {
      return this.aLz;
    }
    HashMap localHashMap = new HashMap();
    LineBoxParcel[] arrayOfLineBoxParcel = this.aLx;
    int k = arrayOfLineBoxParcel.length;
    int i = 0;
    LineBoxParcel localLineBoxParcel;
    if (i < k)
    {
      localLineBoxParcel = arrayOfLineBoxParcel[i];
      if (!localHashMap.containsKey(localLineBoxParcel.aLz)) {
        break label157;
      }
    }
    label157:
    for (int j = ((Integer)localHashMap.get(localLineBoxParcel.aLz)).intValue();; j = 0)
    {
      localHashMap.put(localLineBoxParcel.aLz, Integer.valueOf(j + 1));
      i += 1;
      break;
      this.aLz = ((String)((Map.Entry)Collections.max(localHashMap.entrySet(), new Comparator()
      {
        public int zza(Map.Entry<String, Integer> paramAnonymousEntry1, Map.Entry<String, Integer> paramAnonymousEntry2)
        {
          return ((Integer)paramAnonymousEntry1.getValue()).compareTo((Integer)paramAnonymousEntry2.getValue());
        }
      })).getKey());
      if ((this.aLz == null) || (this.aLz.isEmpty())) {
        this.aLz = "und";
      }
      return this.aLz;
    }
  }
  
  public String getValue()
  {
    if (this.aLx.length == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(this.aLx[0].aLJ);
    int i = 1;
    while (i < this.aLx.length)
    {
      localStringBuilder.append("\n");
      localStringBuilder.append(this.aLx[i].aLJ);
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  void zzclv()
  {
    if (this.aLx.length == 0)
    {
      this.cornerPoints = new Point[0];
      return;
    }
    int m = Integer.MAX_VALUE;
    int n = Integer.MIN_VALUE;
    int i1 = Integer.MAX_VALUE;
    int j = Integer.MIN_VALUE;
    int i = 0;
    while (i < this.aLx.length)
    {
      Point[] arrayOfPoint = zza(this.aLx[i].aLG, this.aLx[0].aLG);
      int k = 0;
      while (k < 4)
      {
        Point localPoint = arrayOfPoint[k];
        i1 = Math.min(i1, localPoint.x);
        n = Math.max(n, localPoint.x);
        m = Math.min(m, localPoint.y);
        j = Math.max(j, localPoint.y);
        k += 1;
      }
      i += 1;
    }
    this.cornerPoints = zza(i1, m, n, j, this.aLx[0].aLG);
  }
  
  List<Line> zzclw()
  {
    if (this.aLx.length == 0) {
      return new ArrayList(0);
    }
    if (this.aLy == null)
    {
      this.aLy = new ArrayList(this.aLx.length);
      LineBoxParcel[] arrayOfLineBoxParcel = this.aLx;
      int j = arrayOfLineBoxParcel.length;
      int i = 0;
      while (i < j)
      {
        LineBoxParcel localLineBoxParcel = arrayOfLineBoxParcel[i];
        this.aLy.add(new Line(localLineBoxParcel));
        i += 1;
      }
    }
    return this.aLy;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/TextBlock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */