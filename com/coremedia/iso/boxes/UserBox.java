package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class UserBox
  extends AbstractBox
{
  public static final String TYPE = "uuid";
  byte[] data;
  
  static {}
  
  public UserBox(byte[] paramArrayOfByte)
  {
    super("uuid", paramArrayOfByte);
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.data = new byte[paramByteBuffer.remaining()];
    paramByteBuffer.get(this.data);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(this.data);
  }
  
  protected long getContentSize()
  {
    return this.data.length;
  }
  
  public byte[] getData()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.data;
  }
  
  public void setData(byte[] paramArrayOfByte)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, paramArrayOfByte);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.data = paramArrayOfByte;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "UserBox[type=" + getType() + ";userType=" + new String(getUserType()) + ";contentLength=" + this.data.length + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/UserBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */