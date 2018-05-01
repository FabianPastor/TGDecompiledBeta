package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class DataEntryUrnBox
  extends AbstractFullBox
{
  public static final String TYPE = "urn ";
  private String location;
  private String name;
  
  static {}
  
  public DataEntryUrnBox()
  {
    super("urn ");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.name = IsoTypeReader.readString(paramByteBuffer);
    this.location = IsoTypeReader.readString(paramByteBuffer);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(Utf8.convert(this.name));
    paramByteBuffer.put((byte)0);
    paramByteBuffer.put(Utf8.convert(this.location));
    paramByteBuffer.put((byte)0);
  }
  
  protected long getContentSize()
  {
    return Utf8.utf8StringLengthInBytes(this.name) + 1 + Utf8.utf8StringLengthInBytes(this.location) + 1;
  }
  
  public String getLocation()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.location;
  }
  
  public String getName()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.name;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "DataEntryUrlBox[name=" + getName() + ";location=" + getLocation() + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/DataEntryUrnBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */