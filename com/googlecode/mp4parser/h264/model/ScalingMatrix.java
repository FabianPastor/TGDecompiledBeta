package com.googlecode.mp4parser.h264.model;

import java.util.Arrays;

public class ScalingMatrix
{
  public ScalingList[] ScalingList4x4;
  public ScalingList[] ScalingList8x8;
  
  public String toString()
  {
    Object localObject2 = null;
    StringBuilder localStringBuilder = new StringBuilder("ScalingMatrix{ScalingList4x4=");
    if (this.ScalingList4x4 == null)
    {
      localObject1 = null;
      localStringBuilder = localStringBuilder.append(localObject1).append("\n").append(", ScalingList8x8=");
      if (this.ScalingList8x8 != null) {
        break label76;
      }
    }
    label76:
    for (Object localObject1 = localObject2;; localObject1 = Arrays.asList(this.ScalingList8x8))
    {
      return localObject1 + "\n" + '}';
      localObject1 = Arrays.asList(this.ScalingList4x4);
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/h264/model/ScalingMatrix.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */