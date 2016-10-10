package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.vision.text.internal.client.WordBoxParcel;
import java.util.ArrayList;
import java.util.List;

public class Element
  implements Text
{
  private WordBoxParcel aLu;
  
  Element(WordBoxParcel paramWordBoxParcel)
  {
    this.aLu = paramWordBoxParcel;
  }
  
  public Rect getBoundingBox()
  {
    return zza.zza(this);
  }
  
  public List<? extends Text> getComponents()
  {
    return new ArrayList();
  }
  
  public Point[] getCornerPoints()
  {
    return zza.zza(this.aLu.aLG);
  }
  
  public String getLanguage()
  {
    return this.aLu.aLz;
  }
  
  public String getValue()
  {
    return this.aLu.aLJ;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/Element.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */