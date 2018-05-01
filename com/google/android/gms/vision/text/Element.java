package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.ft;
import java.util.ArrayList;
import java.util.List;

public class Element
  implements Text
{
  private ft zzbNN;
  
  Element(ft paramft)
  {
    this.zzbNN = paramft;
  }
  
  public Rect getBoundingBox()
  {
    return zzc.zza(this);
  }
  
  public List<? extends Text> getComponents()
  {
    return new ArrayList();
  }
  
  public Point[] getCornerPoints()
  {
    return zzc.zza(this.zzbNN.zzbNY);
  }
  
  public String getLanguage()
  {
    return this.zzbNN.zzbNS;
  }
  
  public String getValue()
  {
    return this.zzbNN.zzbOb;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/Element.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */