package com.googlecode.mp4parser.boxes.mp4;

import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class ESDescriptorBox
  extends AbstractDescriptorBox
{
  static {}
  
  public ESDescriptorBox()
  {
    super("esds");
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, paramObject);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (this == paramObject) {}
    do
    {
      for (;;)
      {
        return bool;
        if ((paramObject != null) && (getClass() == paramObject.getClass())) {
          break;
        }
        bool = false;
      }
      paramObject = (ESDescriptorBox)paramObject;
      if (this.data == null) {
        break;
      }
    } while (this.data.equals(((ESDescriptorBox)paramObject).data));
    for (;;)
    {
      bool = false;
      break;
      if (((ESDescriptorBox)paramObject).data == null) {
        break;
      }
    }
  }
  
  public int hashCode()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (this.data != null) {}
    for (int i = this.data.hashCode();; i = 0) {
      return i;
    }
  }
  
  public void setEsDescriptor(ESDescriptor paramESDescriptor)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramESDescriptor);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    super.setDescriptor(paramESDescriptor);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/ESDescriptorBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */