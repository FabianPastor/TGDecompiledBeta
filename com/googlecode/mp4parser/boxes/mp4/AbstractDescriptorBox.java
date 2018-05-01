package com.googlecode.mp4parser.boxes.mp4;

import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ObjectDescriptorFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class AbstractDescriptorBox
  extends AbstractFullBox
{
  private static Logger log = Logger.getLogger(AbstractDescriptorBox.class.getName());
  protected ByteBuffer data;
  protected BaseDescriptor descriptor;
  
  static {}
  
  public AbstractDescriptorBox(String paramString)
  {
    super(paramString);
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.data = paramByteBuffer.slice();
    paramByteBuffer.position(paramByteBuffer.position() + paramByteBuffer.remaining());
    try
    {
      this.data.rewind();
      this.descriptor = ObjectDescriptorFactory.createFrom(-1, this.data);
      return;
    }
    catch (IOException paramByteBuffer)
    {
      for (;;)
      {
        log.log(Level.WARNING, "Error parsing ObjectDescriptor", paramByteBuffer);
      }
    }
    catch (IndexOutOfBoundsException paramByteBuffer)
    {
      for (;;)
      {
        log.log(Level.WARNING, "Error parsing ObjectDescriptor", paramByteBuffer);
      }
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    this.data.rewind();
    paramByteBuffer.put(this.data);
  }
  
  protected long getContentSize()
  {
    return this.data.limit() + 4;
  }
  
  public void setData(ByteBuffer paramByteBuffer)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this, paramByteBuffer);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.data = paramByteBuffer;
  }
  
  public void setDescriptor(BaseDescriptor paramBaseDescriptor)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramBaseDescriptor);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.descriptor = paramBaseDescriptor;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/AbstractDescriptorBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */