package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class ItemDataBox
  extends AbstractBox
{
  public static final String TYPE = "idat";
  ByteBuffer data = ByteBuffer.allocate(0);
  
  static {}
  
  public ItemDataBox()
  {
    super("idat");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.data = paramByteBuffer.slice();
    paramByteBuffer.position(paramByteBuffer.position() + paramByteBuffer.remaining());
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(this.data);
  }
  
  protected long getContentSize()
  {
    return this.data.limit();
  }
  
  public ByteBuffer getData()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.data;
  }
  
  public void setData(ByteBuffer paramByteBuffer)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramByteBuffer);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.data = paramByteBuffer;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/ItemDataBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */