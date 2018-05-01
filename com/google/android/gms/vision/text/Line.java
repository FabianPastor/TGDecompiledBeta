package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.fe;
import com.google.android.gms.internal.fk;
import com.google.android.gms.internal.ft;
import java.util.ArrayList;
import java.util.List;

public class Line
  implements Text
{
  private fk zzbNO;
  private List<Element> zzbNP;
  
  Line(fk paramfk)
  {
    this.zzbNO = paramfk;
  }
  
  public float getAngle()
  {
    return this.zzbNO.zzbNY.zzbNW;
  }
  
  public Rect getBoundingBox()
  {
    return zzc.zza(this);
  }
  
  public List<? extends Text> getComponents()
  {
    if (this.zzbNO.zzbNX.length == 0) {
      return new ArrayList(0);
    }
    if (this.zzbNP == null)
    {
      this.zzbNP = new ArrayList(this.zzbNO.zzbNX.length);
      ft[] arrayOfft = this.zzbNO.zzbNX;
      int j = arrayOfft.length;
      int i = 0;
      while (i < j)
      {
        ft localft = arrayOfft[i];
        this.zzbNP.add(new Element(localft));
        i += 1;
      }
    }
    return this.zzbNP;
  }
  
  public Point[] getCornerPoints()
  {
    return zzc.zza(this.zzbNO.zzbNY);
  }
  
  public String getLanguage()
  {
    return this.zzbNO.zzbNS;
  }
  
  public String getValue()
  {
    return this.zzbNO.zzbOb;
  }
  
  public boolean isVertical()
  {
    return this.zzbNO.zzbOe;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/Line.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */