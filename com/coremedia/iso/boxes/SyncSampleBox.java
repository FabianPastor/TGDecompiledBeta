package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class SyncSampleBox
  extends AbstractFullBox
{
  public static final String TYPE = "stss";
  private long[] sampleNumber;
  
  static {}
  
  public SyncSampleBox()
  {
    super("stss");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int j = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.sampleNumber = new long[j];
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      this.sampleNumber[i] = IsoTypeReader.readUInt32(paramByteBuffer);
      i += 1;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleNumber.length);
    long[] arrayOfLong = this.sampleNumber;
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
  
  protected long getContentSize()
  {
    return this.sampleNumber.length * 4 + 8;
  }
  
  public long[] getSampleNumber()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.sampleNumber;
  }
  
  public void setSampleNumber(long[] paramArrayOfLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, paramArrayOfLong);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.sampleNumber = paramArrayOfLong;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "SyncSampleBox[entryCount=" + this.sampleNumber.length + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/SyncSampleBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */