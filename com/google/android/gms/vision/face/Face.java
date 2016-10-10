package com.google.android.gms.vision.face;

import android.graphics.PointF;
import java.util.Arrays;
import java.util.List;

public class Face
{
  public static final float UNCOMPUTED_PROBABILITY = -1.0F;
  private PointF aKP;
  private float aKQ;
  private float aKR;
  private List<Landmark> aKS;
  private float aKT;
  private float aKU;
  private float aKV;
  private float amJ;
  private float amK;
  private int mId;
  
  public Face(int paramInt, PointF paramPointF, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Landmark[] paramArrayOfLandmark, float paramFloat5, float paramFloat6, float paramFloat7)
  {
    this.mId = paramInt;
    this.aKP = paramPointF;
    this.amJ = paramFloat1;
    this.amK = paramFloat2;
    this.aKQ = paramFloat3;
    this.aKR = paramFloat4;
    this.aKS = Arrays.asList(paramArrayOfLandmark);
    if ((paramFloat5 >= 0.0F) && (paramFloat5 <= 1.0F))
    {
      this.aKT = paramFloat5;
      if ((paramFloat6 < 0.0F) || (paramFloat6 > 1.0F)) {
        break label116;
      }
    }
    label116:
    for (this.aKU = paramFloat6;; this.aKU = -1.0F)
    {
      if ((paramFloat7 < 0.0F) || (paramFloat7 > 1.0F)) {
        break label125;
      }
      this.aKV = paramFloat7;
      return;
      this.aKT = -1.0F;
      break;
    }
    label125:
    this.aKV = -1.0F;
  }
  
  public float getEulerY()
  {
    return this.aKQ;
  }
  
  public float getEulerZ()
  {
    return this.aKR;
  }
  
  public float getHeight()
  {
    return this.amK;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public float getIsLeftEyeOpenProbability()
  {
    return this.aKT;
  }
  
  public float getIsRightEyeOpenProbability()
  {
    return this.aKU;
  }
  
  public float getIsSmilingProbability()
  {
    return this.aKV;
  }
  
  public List<Landmark> getLandmarks()
  {
    return this.aKS;
  }
  
  public PointF getPosition()
  {
    return new PointF(this.aKP.x - this.amJ / 2.0F, this.aKP.y - this.amK / 2.0F);
  }
  
  public float getWidth()
  {
    return this.amJ;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/Face.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */