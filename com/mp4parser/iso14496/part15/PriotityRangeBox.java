package com.mp4parser.iso14496.part15;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class PriotityRangeBox
  extends AbstractBox
{
  public static final String TYPE = "svpr";
  int max_priorityId;
  int min_priorityId;
  int reserved1 = 0;
  int reserved2 = 0;
  
  static {}
  
  public PriotityRangeBox()
  {
    super("svpr");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.min_priorityId = IsoTypeReader.readUInt8(paramByteBuffer);
    this.reserved1 = ((this.min_priorityId & 0xC0) >> 6);
    this.min_priorityId &= 0x3F;
    this.max_priorityId = IsoTypeReader.readUInt8(paramByteBuffer);
    this.reserved2 = ((this.max_priorityId & 0xC0) >> 6);
    this.max_priorityId &= 0x3F;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt8(paramByteBuffer, (this.reserved1 << 6) + this.min_priorityId);
    IsoTypeWriter.writeUInt8(paramByteBuffer, (this.reserved2 << 6) + this.max_priorityId);
  }
  
  protected long getContentSize()
  {
    return 2L;
  }
  
  public int getMax_priorityId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.max_priorityId;
  }
  
  public int getMin_priorityId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.min_priorityId;
  }
  
  public int getReserved1()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved1;
  }
  
  public int getReserved2()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved2;
  }
  
  public void setMax_priorityId(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.max_priorityId = paramInt;
  }
  
  public void setMin_priorityId(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.min_priorityId = paramInt;
  }
  
  public void setReserved1(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reserved1 = paramInt;
  }
  
  public void setReserved2(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reserved2 = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part15/PriotityRangeBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */