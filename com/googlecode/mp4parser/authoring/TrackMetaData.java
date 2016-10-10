package com.googlecode.mp4parser.authoring;

import com.googlecode.mp4parser.util.Matrix;
import java.util.Date;

public class TrackMetaData
  implements Cloneable
{
  private Date creationTime = new Date();
  private int group = 0;
  private double height;
  private String language = "eng";
  int layer;
  private Matrix matrix = Matrix.ROTATE_0;
  private Date modificationTime = new Date();
  private long timescale;
  private long trackId = 1L;
  private float volume;
  private double width;
  
  public Object clone()
  {
    try
    {
      Object localObject = super.clone();
      return localObject;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public Date getCreationTime()
  {
    return this.creationTime;
  }
  
  public int getGroup()
  {
    return this.group;
  }
  
  public double getHeight()
  {
    return this.height;
  }
  
  public String getLanguage()
  {
    return this.language;
  }
  
  public int getLayer()
  {
    return this.layer;
  }
  
  public Matrix getMatrix()
  {
    return this.matrix;
  }
  
  public Date getModificationTime()
  {
    return this.modificationTime;
  }
  
  public long getTimescale()
  {
    return this.timescale;
  }
  
  public long getTrackId()
  {
    return this.trackId;
  }
  
  public float getVolume()
  {
    return this.volume;
  }
  
  public double getWidth()
  {
    return this.width;
  }
  
  public void setCreationTime(Date paramDate)
  {
    this.creationTime = paramDate;
  }
  
  public void setGroup(int paramInt)
  {
    this.group = paramInt;
  }
  
  public void setHeight(double paramDouble)
  {
    this.height = paramDouble;
  }
  
  public void setLanguage(String paramString)
  {
    this.language = paramString;
  }
  
  public void setLayer(int paramInt)
  {
    this.layer = paramInt;
  }
  
  public void setMatrix(Matrix paramMatrix)
  {
    this.matrix = paramMatrix;
  }
  
  public void setModificationTime(Date paramDate)
  {
    this.modificationTime = paramDate;
  }
  
  public void setTimescale(long paramLong)
  {
    this.timescale = paramLong;
  }
  
  public void setTrackId(long paramLong)
  {
    this.trackId = paramLong;
  }
  
  public void setVolume(float paramFloat)
  {
    this.volume = paramFloat;
  }
  
  public void setWidth(double paramDouble)
  {
    this.width = paramDouble;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/TrackMetaData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */