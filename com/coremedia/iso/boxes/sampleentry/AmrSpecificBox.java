package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class AmrSpecificBox
  extends AbstractBox
{
  public static final String TYPE = "damr";
  private int decoderVersion;
  private int framesPerSample;
  private int modeChangePeriod;
  private int modeSet;
  private String vendor;
  
  static {}
  
  public AmrSpecificBox()
  {
    super("damr");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    byte[] arrayOfByte = new byte[4];
    paramByteBuffer.get(arrayOfByte);
    this.vendor = IsoFile.bytesToFourCC(arrayOfByte);
    this.decoderVersion = IsoTypeReader.readUInt8(paramByteBuffer);
    this.modeSet = IsoTypeReader.readUInt16(paramByteBuffer);
    this.modeChangePeriod = IsoTypeReader.readUInt8(paramByteBuffer);
    this.framesPerSample = IsoTypeReader.readUInt8(paramByteBuffer);
  }
  
  public void getContent(ByteBuffer paramByteBuffer)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, paramByteBuffer);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.vendor));
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.decoderVersion);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.modeSet);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.modeChangePeriod);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.framesPerSample);
  }
  
  protected long getContentSize()
  {
    return 9L;
  }
  
  public int getDecoderVersion()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.decoderVersion;
  }
  
  public int getFramesPerSample()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.framesPerSample;
  }
  
  public int getModeChangePeriod()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.modeChangePeriod;
  }
  
  public int getModeSet()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.modeSet;
  }
  
  public String getVendor()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.vendor;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("AmrSpecificBox[vendor=").append(getVendor());
    ((StringBuilder)localObject).append(";decoderVersion=").append(getDecoderVersion());
    ((StringBuilder)localObject).append(";modeSet=").append(getModeSet());
    ((StringBuilder)localObject).append(";modeChangePeriod=").append(getModeChangePeriod());
    ((StringBuilder)localObject).append(";framesPerSample=").append(getFramesPerSample());
    ((StringBuilder)localObject).append("]");
    return ((StringBuilder)localObject).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/sampleentry/AmrSpecificBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */