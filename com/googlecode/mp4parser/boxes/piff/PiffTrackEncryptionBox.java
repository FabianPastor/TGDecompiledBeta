package com.googlecode.mp4parser.boxes.piff;

import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.boxes.AbstractTrackEncryptionBox;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class PiffTrackEncryptionBox
  extends AbstractTrackEncryptionBox
{
  static {}
  
  public PiffTrackEncryptionBox()
  {
    super("uuid");
  }
  
  public int getFlags()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return 0;
  }
  
  public byte[] getUserType()
  {
    return new byte[] { -119, 116, -37, -50, 123, -25, 76, 81, -124, -7, 113, 72, -7, -120, 37, 84 };
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/piff/PiffTrackEncryptionBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */