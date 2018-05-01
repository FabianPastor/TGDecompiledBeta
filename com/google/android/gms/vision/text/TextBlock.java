package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.SparseArray;
import com.google.android.gms.internal.fe;
import com.google.android.gms.internal.fk;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class TextBlock
  implements Text
{
  private Point[] cornerPoints;
  private fk[] zzbNQ;
  private List<Line> zzbNR;
  private String zzbNS;
  private Rect zzbNT;
  
  TextBlock(SparseArray<fk> paramSparseArray)
  {
    this.zzbNQ = new fk[paramSparseArray.size()];
    int i = 0;
    while (i < this.zzbNQ.length)
    {
      this.zzbNQ[i] = ((fk)paramSparseArray.valueAt(i));
      i += 1;
    }
  }
  
  private static Point[] zza(int paramInt1, int paramInt2, int paramInt3, int paramInt4, fe paramfe)
  {
    int i = paramfe.left;
    int j = paramfe.top;
    double d1 = Math.sin(Math.toRadians(paramfe.zzbNW));
    double d2 = Math.cos(Math.toRadians(paramfe.zzbNW));
    paramfe = new Point[4];
    paramfe[0] = new Point(paramInt1, paramInt2);
    paramfe[1] = new Point(paramInt3, paramInt2);
    paramfe[2] = new Point(paramInt3, paramInt4);
    paramfe[3] = new Point(paramInt1, paramInt4);
    paramInt1 = 0;
    while (paramInt1 < 4)
    {
      paramInt2 = (int)(paramfe[paramInt1].x * d2 - paramfe[paramInt1].y * d1);
      paramInt3 = (int)(paramfe[paramInt1].x * d1 + paramfe[paramInt1].y * d2);
      paramfe[paramInt1].x = paramInt2;
      paramfe[paramInt1].y = paramInt3;
      paramfe[paramInt1].offset(i, j);
      paramInt1 += 1;
    }
    return paramfe;
  }
  
  public Rect getBoundingBox()
  {
    if (this.zzbNT == null) {
      this.zzbNT = zzc.zza(this);
    }
    return this.zzbNT;
  }
  
  public List<? extends Text> getComponents()
  {
    if (this.zzbNQ.length == 0) {
      return new ArrayList(0);
    }
    if (this.zzbNR == null)
    {
      this.zzbNR = new ArrayList(this.zzbNQ.length);
      fk[] arrayOffk = this.zzbNQ;
      int j = arrayOffk.length;
      int i = 0;
      while (i < j)
      {
        fk localfk = arrayOffk[i];
        this.zzbNR.add(new Line(localfk));
        i += 1;
      }
    }
    return this.zzbNR;
  }
  
  public Point[] getCornerPoints()
  {
    if (this.cornerPoints == null) {
      if (this.zzbNQ.length != 0) {
        break label28;
      }
    }
    label28:
    int n;
    int m;
    int k;
    int j;
    for (this.cornerPoints = new Point[0];; this.cornerPoints = zza(n, k, m, j, this.zzbNQ[0].zzbNY))
    {
      return this.cornerPoints;
      n = Integer.MAX_VALUE;
      m = Integer.MIN_VALUE;
      k = Integer.MAX_VALUE;
      j = Integer.MIN_VALUE;
      int i = 0;
      while (i < this.zzbNQ.length)
      {
        fe localfe = this.zzbNQ[i].zzbNY;
        Object localObject = this.zzbNQ[0].zzbNY;
        int i1 = -((fe)localObject).left;
        int i2 = -((fe)localObject).top;
        double d1 = Math.sin(Math.toRadians(((fe)localObject).zzbNW));
        double d2 = Math.cos(Math.toRadians(((fe)localObject).zzbNW));
        localObject = new Point[4];
        localObject[0] = new Point(localfe.left, localfe.top);
        localObject[0].offset(i1, i2);
        i1 = (int)(localObject[0].x * d2 + localObject[0].y * d1);
        i2 = (int)(d1 * -localObject[0].x + d2 * localObject[0].y);
        localObject[0].x = i1;
        localObject[0].y = i2;
        localObject[1] = new Point(localfe.width + i1, i2);
        localObject[2] = new Point(localfe.width + i1, localfe.height + i2);
        localObject[3] = new Point(i1, localfe.height + i2);
        i1 = 0;
        while (i1 < 4)
        {
          localfe = localObject[i1];
          n = Math.min(n, localfe.x);
          m = Math.max(m, localfe.x);
          k = Math.min(k, localfe.y);
          j = Math.max(j, localfe.y);
          i1 += 1;
        }
        i += 1;
      }
    }
  }
  
  public String getLanguage()
  {
    if (this.zzbNS != null) {
      return this.zzbNS;
    }
    HashMap localHashMap = new HashMap();
    fk[] arrayOffk = this.zzbNQ;
    int k = arrayOffk.length;
    int i = 0;
    fk localfk;
    if (i < k)
    {
      localfk = arrayOffk[i];
      if (!localHashMap.containsKey(localfk.zzbNS)) {
        break label157;
      }
    }
    label157:
    for (int j = ((Integer)localHashMap.get(localfk.zzbNS)).intValue();; j = 0)
    {
      localHashMap.put(localfk.zzbNS, Integer.valueOf(j + 1));
      i += 1;
      break;
      this.zzbNS = ((String)((Map.Entry)Collections.max(localHashMap.entrySet(), new zza(this))).getKey());
      if ((this.zzbNS == null) || (this.zzbNS.isEmpty())) {
        this.zzbNS = "und";
      }
      return this.zzbNS;
    }
  }
  
  public String getValue()
  {
    if (this.zzbNQ.length == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(this.zzbNQ[0].zzbOb);
    int i = 1;
    while (i < this.zzbNQ.length)
    {
      localStringBuilder.append("\n");
      localStringBuilder.append(this.zzbNQ[i].zzbOb);
      i += 1;
    }
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/TextBlock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */