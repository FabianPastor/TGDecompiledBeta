package com.googlecode.mp4parser.boxes.piff;

import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class PiffSampleEncryptionBox
  extends AbstractSampleEncryptionBox
{
  static {}
  
  public PiffSampleEncryptionBox()
  {
    super("uuid");
  }
  
  public int getAlgorithmId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.algorithmId;
  }
  
  public int getIvSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.ivSize;
  }
  
  public byte[] getKid()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.kid;
  }
  
  public byte[] getUserType()
  {
    return new byte[] { -94, 57, 79, 82, 90, -101, 79, 20, -94, 68, 108, 66, 124, 100, -115, -12 };
  }
  
  @DoNotParseDetail
  public boolean isOverrideTrackEncryptionBoxParameters()
  {
    return (getFlags() & 0x1) > 0;
  }
  
  public void setAlgorithmId(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.algorithmId = paramInt;
  }
  
  public void setIvSize(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.ivSize = paramInt;
  }
  
  public void setKid(byte[] paramArrayOfByte)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, paramArrayOfByte);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.kid = paramArrayOfByte;
  }
  
  @DoNotParseDetail
  public void setOverrideTrackEncryptionBoxParameters(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      setFlags(getFlags() | 0x1);
      return;
    }
    setFlags(getFlags() & 0xFFFFFE);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/piff/PiffSampleEncryptionBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */