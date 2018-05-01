package org.telegram.messenger.exoplayer2.video;

import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil.SpsData;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class AvcConfig
{
  public final int height;
  public final List<byte[]> initializationData;
  public final int nalUnitLengthFieldLength;
  public final float pixelWidthAspectRatio;
  public final int width;
  
  private AvcConfig(List<byte[]> paramList, int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    this.initializationData = paramList;
    this.nalUnitLengthFieldLength = paramInt1;
    this.width = paramInt2;
    this.height = paramInt3;
    this.pixelWidthAspectRatio = paramFloat;
  }
  
  private static byte[] buildNalUnitForChild(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.readUnsignedShort();
    int j = paramParsableByteArray.getPosition();
    paramParsableByteArray.skipBytes(i);
    return CodecSpecificDataUtil.buildNalUnit(paramParsableByteArray.data, j, i);
  }
  
  public static AvcConfig parse(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    int i;
    try
    {
      paramParsableByteArray.skipBytes(4);
      i = (paramParsableByteArray.readUnsignedByte() & 0x3) + 1;
      if (i == 3)
      {
        paramParsableByteArray = new java/lang/IllegalStateException;
        paramParsableByteArray.<init>();
        throw paramParsableByteArray;
      }
    }
    catch (ArrayIndexOutOfBoundsException paramParsableByteArray)
    {
      throw new ParserException("Error parsing AVC config", paramParsableByteArray);
    }
    ArrayList localArrayList = new java/util/ArrayList;
    localArrayList.<init>();
    int j = paramParsableByteArray.readUnsignedByte() & 0x1F;
    for (int k = 0; k < j; k++) {
      localArrayList.add(buildNalUnitForChild(paramParsableByteArray));
    }
    int m = paramParsableByteArray.readUnsignedByte();
    for (k = 0; k < m; k++) {
      localArrayList.add(buildNalUnitForChild(paramParsableByteArray));
    }
    m = -1;
    k = -1;
    float f = 1.0F;
    if (j > 0)
    {
      paramParsableByteArray = (byte[])localArrayList.get(0);
      paramParsableByteArray = NalUnitUtil.parseSpsNalUnit((byte[])localArrayList.get(0), i, paramParsableByteArray.length);
      m = paramParsableByteArray.width;
      k = paramParsableByteArray.height;
      f = paramParsableByteArray.pixelWidthAspectRatio;
    }
    paramParsableByteArray = new AvcConfig(localArrayList, i, m, k, f);
    return paramParsableByteArray;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/video/AvcConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */