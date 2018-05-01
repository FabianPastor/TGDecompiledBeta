package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public final class OmaDrmAccessUnitFormatBox
  extends AbstractFullBox
{
  public static final String TYPE = "odaf";
  private byte allBits;
  private int initVectorLength;
  private int keyIndicatorLength;
  private boolean selectiveEncryption;
  
  static {}
  
  public OmaDrmAccessUnitFormatBox()
  {
    super("odaf");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.allBits = ((byte)(byte)IsoTypeReader.readUInt8(paramByteBuffer));
    if ((this.allBits & 0x80) == 128) {}
    for (boolean bool = true;; bool = false)
    {
      this.selectiveEncryption = bool;
      this.keyIndicatorLength = IsoTypeReader.readUInt8(paramByteBuffer);
      this.initVectorLength = IsoTypeReader.readUInt8(paramByteBuffer);
      return;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.allBits);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.keyIndicatorLength);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.initVectorLength);
  }
  
  protected long getContentSize()
  {
    return 7L;
  }
  
  public int getInitVectorLength()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.initVectorLength;
  }
  
  public int getKeyIndicatorLength()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.keyIndicatorLength;
  }
  
  public boolean isSelectiveEncryption()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.selectiveEncryption;
  }
  
  public void setAllBits(byte paramByte)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.byteObject(paramByte));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.allBits = paramByte;
    if ((paramByte & 0x80) == 128) {}
    for (boolean bool = true;; bool = false)
    {
      this.selectiveEncryption = bool;
      return;
    }
  }
  
  public void setInitVectorLength(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.initVectorLength = paramInt;
  }
  
  public void setKeyIndicatorLength(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.keyIndicatorLength = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/OmaDrmAccessUnitFormatBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */