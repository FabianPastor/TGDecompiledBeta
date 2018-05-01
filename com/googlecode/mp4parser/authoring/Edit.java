package com.googlecode.mp4parser.authoring;

public class Edit
{
  private double mediaRate;
  private long mediaTime;
  private double segmentDuration;
  private long timeScale;
  
  public Edit(long paramLong1, long paramLong2, double paramDouble1, double paramDouble2)
  {
    this.timeScale = paramLong2;
    this.segmentDuration = paramDouble2;
    this.mediaTime = paramLong1;
    this.mediaRate = paramDouble1;
  }
  
  public double getMediaRate()
  {
    return this.mediaRate;
  }
  
  public long getMediaTime()
  {
    return this.mediaTime;
  }
  
  public double getSegmentDuration()
  {
    return this.segmentDuration;
  }
  
  public long getTimeScale()
  {
    return this.timeScale;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/Edit.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */