package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class SampleSizeBox
  extends AbstractFullBox
{
  public static final String TYPE = "stsz";
  int sampleCount;
  private long sampleSize;
  private long[] sampleSizes = new long[0];
  
  static {}
  
  public SampleSizeBox()
  {
    super("stsz");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.sampleSize = IsoTypeReader.readUInt32(paramByteBuffer);
    this.sampleCount = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    int i;
    if (this.sampleSize == 0L)
    {
      this.sampleSizes = new long[this.sampleCount];
      i = 0;
    }
    for (;;)
    {
      if (i >= this.sampleCount) {
        return;
      }
      this.sampleSizes[i] = IsoTypeReader.readUInt32(paramByteBuffer);
      i += 1;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleSize);
    if (this.sampleSize == 0L)
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleSizes.length);
      long[] arrayOfLong = this.sampleSizes;
      int j = arrayOfLong.length;
      int i = 0;
      for (;;)
      {
        if (i >= j) {
          return;
        }
        IsoTypeWriter.writeUInt32(paramByteBuffer, arrayOfLong[i]);
        i += 1;
      }
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleCount);
  }
  
  protected long getContentSize()
  {
    if (this.sampleSize == 0L) {}
    for (int i = this.sampleSizes.length * 4;; i = 0) {
      return i + 12;
    }
  }
  
  public long getSampleCount()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (this.sampleSize > 0L) {
      return this.sampleCount;
    }
    return this.sampleSizes.length;
  }
  
  public long getSampleSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.sampleSize;
  }
  
  public long getSampleSizeAtIndex(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (this.sampleSize > 0L) {
      return this.sampleSize;
    }
    return this.sampleSizes[paramInt];
  }
  
  public long[] getSampleSizes()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.sampleSizes;
  }
  
  public void setSampleSize(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.sampleSize = paramLong;
  }
  
  public void setSampleSizes(long[] paramArrayOfLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, paramArrayOfLong);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.sampleSizes = paramArrayOfLong;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "SampleSizeBox[sampleSize=" + getSampleSize() + ";sampleCount=" + getSampleCount() + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/SampleSizeBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */