package com.googlecode.mp4parser.boxes.mp4;

import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class ESDescriptorBox
  extends AbstractDescriptorBox
{
  public static final String TYPE = "esds";
  
  static {}
  
  public ESDescriptorBox()
  {
    super("esds");
  }
  
  public boolean equals(Object paramObject)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, paramObject);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (ESDescriptorBox)paramObject;
      if (this.data == null) {
        break;
      }
    } while (this.data.equals(((ESDescriptorBox)paramObject).data));
    for (;;)
    {
      return false;
      if (((ESDescriptorBox)paramObject).data == null) {
        break;
      }
    }
  }
  
  public ESDescriptor getEsDescriptor()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return (ESDescriptor)super.getDescriptor();
  }
  
  public int hashCode()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (this.data != null) {
      return this.data.hashCode();
    }
    return 0;
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