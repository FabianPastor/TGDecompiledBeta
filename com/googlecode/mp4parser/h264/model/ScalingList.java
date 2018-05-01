package com.googlecode.mp4parser.h264.model;

import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.googlecode.mp4parser.h264.write.CAVLCWriter;
import java.io.IOException;

public class ScalingList
{
  public int[] scalingList;
  public boolean useDefaultScalingMatrixFlag;
  
  public static ScalingList read(CAVLCReader paramCAVLCReader, int paramInt)
    throws IOException
  {
    ScalingList localScalingList = new ScalingList();
    localScalingList.scalingList = new int[paramInt];
    int j = 8;
    int m = 8;
    int k = 0;
    if (k >= paramInt) {
      return localScalingList;
    }
    int i = m;
    boolean bool;
    label73:
    int[] arrayOfInt;
    if (m != 0)
    {
      i = (j + paramCAVLCReader.readSE("deltaScale") + 256) % 256;
      if ((k == 0) && (i == 0))
      {
        bool = true;
        localScalingList.useDefaultScalingMatrixFlag = bool;
      }
    }
    else
    {
      arrayOfInt = localScalingList.scalingList;
      if (i != 0) {
        break label124;
      }
    }
    for (;;)
    {
      arrayOfInt[k] = j;
      j = localScalingList.scalingList[k];
      k += 1;
      m = i;
      break;
      bool = false;
      break label73;
      label124:
      j = i;
    }
  }
  
  public String toString()
  {
    return "ScalingList{scalingList=" + this.scalingList + ", useDefaultScalingMatrixFlag=" + this.useDefaultScalingMatrixFlag + '}';
  }
  
  public void write(CAVLCWriter paramCAVLCWriter)
    throws IOException
  {
    if (this.useDefaultScalingMatrixFlag) {
      paramCAVLCWriter.writeSE(0, "SPS: ");
    }
    for (;;)
    {
      return;
      int j = 8;
      int i = 0;
      while (i < this.scalingList.length)
      {
        if (8 != 0) {
          paramCAVLCWriter.writeSE(this.scalingList[i] - j - 256, "SPS: ");
        }
        j = this.scalingList[i];
        i += 1;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/h264/model/ScalingList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */