package com.google.android.gms.vision.face;

import android.graphics.PointF;
import java.util.Arrays;
import java.util.List;

public class Face
{
  private int mId;
  private PointF zzbd;
  private float zzbe;
  private float zzbf;
  private float zzbg;
  private float zzbh;
  private List<Landmark> zzbi;
  private float zzbj;
  private float zzbk;
  private float zzbl;
  
  public Face(int paramInt, PointF paramPointF, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Landmark[] paramArrayOfLandmark, float paramFloat5, float paramFloat6, float paramFloat7)
  {
    this.mId = paramInt;
    this.zzbd = paramPointF;
    this.zzbe = paramFloat1;
    this.zzbf = paramFloat2;
    this.zzbg = paramFloat3;
    this.zzbh = paramFloat4;
    this.zzbi = Arrays.asList(paramArrayOfLandmark);
    if ((paramFloat5 >= 0.0F) && (paramFloat5 <= 1.0F))
    {
      this.zzbj = paramFloat5;
      if ((paramFloat6 < 0.0F) || (paramFloat6 > 1.0F)) {
        break label116;
      }
      this.zzbk = paramFloat6;
      label86:
      if ((paramFloat7 < 0.0F) || (paramFloat7 > 1.0F)) {
        break label125;
      }
    }
    label116:
    label125:
    for (this.zzbl = paramFloat7;; this.zzbl = -1.0F)
    {
      return;
      this.zzbj = -1.0F;
      break;
      this.zzbk = -1.0F;
      break label86;
    }
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public List<Landmark> getLandmarks()
  {
    return this.zzbi;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/Face.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */