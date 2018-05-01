package com.googlecode.mp4parser.h264.model;

public class AspectRatio
{
  public static final AspectRatio Extended_SAR = new AspectRatio(255);
  private int value;
  
  private AspectRatio(int paramInt)
  {
    this.value = paramInt;
  }
  
  public static AspectRatio fromValue(int paramInt)
  {
    if (paramInt == Extended_SAR.value) {
      return Extended_SAR;
    }
    return new AspectRatio(paramInt);
  }
  
  public int getValue()
  {
    return this.value;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("AspectRatio{");
    localStringBuilder.append("value=").append(this.value);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/h264/model/AspectRatio.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */