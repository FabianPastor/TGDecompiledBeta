package com.googlecode.mp4parser;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.FullBox;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public abstract class AbstractFullBox
  extends AbstractBox
  implements FullBox
{
  private int flags;
  private int version;
  
  static {}
  
  protected AbstractFullBox(String paramString)
  {
    super(paramString);
  }
  
  protected AbstractFullBox(String paramString, byte[] paramArrayOfByte)
  {
    super(paramString, paramArrayOfByte);
  }
  
  @DoNotParseDetail
  public int getFlags()
  {
    if (!this.isParsed) {
      parseDetails();
    }
    return this.flags;
  }
  
  @DoNotParseDetail
  public int getVersion()
  {
    if (!this.isParsed) {
      parseDetails();
    }
    return this.version;
  }
  
  protected final long parseVersionAndFlags(ByteBuffer paramByteBuffer)
  {
    this.version = IsoTypeReader.readUInt8(paramByteBuffer);
    this.flags = IsoTypeReader.readUInt24(paramByteBuffer);
    return 4L;
  }
  
  public void setFlags(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.flags = paramInt;
  }
  
  public void setVersion(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.version = paramInt;
  }
  
  protected final void writeVersionAndFlags(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.version);
    IsoTypeWriter.writeUInt24(paramByteBuffer, this.flags);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/AbstractFullBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */