package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.vision.text.internal.client.BoundingBoxParcel;
import com.google.android.gms.vision.text.internal.client.LineBoxParcel;
import com.google.android.gms.vision.text.internal.client.WordBoxParcel;
import java.util.ArrayList;
import java.util.List;

public class Line
  implements Text
{
  private LineBoxParcel aLv;
  private List<Element> aLw;
  
  Line(LineBoxParcel paramLineBoxParcel)
  {
    this.aLv = paramLineBoxParcel;
  }
  
  public float getAngle()
  {
    return this.aLv.aLG.aLE;
  }
  
  public Rect getBoundingBox()
  {
    return zza.zza(this);
  }
  
  public List<? extends Text> getComponents()
  {
    return zzclu();
  }
  
  public Point[] getCornerPoints()
  {
    return zza.zza(this.aLv.aLG);
  }
  
  public String getLanguage()
  {
    return this.aLv.aLz;
  }
  
  public String getValue()
  {
    return this.aLv.aLJ;
  }
  
  public boolean isVertical()
  {
    return this.aLv.aLM;
  }
  
  List<Element> zzclu()
  {
    if (this.aLv.aLF.length == 0) {
      return new ArrayList(0);
    }
    if (this.aLw == null)
    {
      this.aLw = new ArrayList(this.aLv.aLF.length);
      WordBoxParcel[] arrayOfWordBoxParcel = this.aLv.aLF;
      int j = arrayOfWordBoxParcel.length;
      int i = 0;
      while (i < j)
      {
        WordBoxParcel localWordBoxParcel = arrayOfWordBoxParcel[i];
        this.aLw.add(new Element(localWordBoxParcel));
        i += 1;
      }
    }
    return this.aLw;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/Line.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */