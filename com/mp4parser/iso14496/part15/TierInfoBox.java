package com.mp4parser.iso14496.part15;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class TierInfoBox
  extends AbstractBox
{
  public static final String TYPE = "tiri";
  int constantFrameRate;
  int discardable;
  int frameRate;
  int levelIndication;
  int profileIndication;
  int profile_compatibility;
  int reserved1 = 0;
  int reserved2 = 0;
  int tierID;
  int visualHeight;
  int visualWidth;
  
  static {}
  
  public TierInfoBox()
  {
    super("tiri");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.tierID = IsoTypeReader.readUInt16(paramByteBuffer);
    this.profileIndication = IsoTypeReader.readUInt8(paramByteBuffer);
    this.profile_compatibility = IsoTypeReader.readUInt8(paramByteBuffer);
    this.levelIndication = IsoTypeReader.readUInt8(paramByteBuffer);
    this.reserved1 = IsoTypeReader.readUInt8(paramByteBuffer);
    this.visualWidth = IsoTypeReader.readUInt16(paramByteBuffer);
    this.visualHeight = IsoTypeReader.readUInt16(paramByteBuffer);
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    this.discardable = ((i & 0xC0) >> 6);
    this.constantFrameRate = ((i & 0x30) >> 4);
    this.reserved2 = (i & 0xF);
    this.frameRate = IsoTypeReader.readUInt16(paramByteBuffer);
  }
  
  public int getConstantFrameRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_16, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.constantFrameRate;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.tierID);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.profileIndication);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.profile_compatibility);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.levelIndication);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.reserved1);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.visualWidth);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.visualHeight);
    IsoTypeWriter.writeUInt8(paramByteBuffer, (this.discardable << 6) + (this.constantFrameRate << 4) + this.reserved2);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.frameRate);
  }
  
  protected long getContentSize()
  {
    return 13L;
  }
  
  public int getDiscardable()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.discardable;
  }
  
  public int getFrameRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_20, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.frameRate;
  }
  
  public int getLevelIndication()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.levelIndication;
  }
  
  public int getProfileIndication()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.profileIndication;
  }
  
  public int getProfile_compatibility()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.profile_compatibility;
  }
  
  public int getReserved1()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved1;
  }
  
  public int getReserved2()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_18, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved2;
  }
  
  public int getTierID()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.tierID;
  }
  
  public int getVisualHeight()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.visualHeight;
  }
  
  public int getVisualWidth()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.visualWidth;
  }
  
  public void setConstantFrameRate(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_17, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.constantFrameRate = paramInt;
  }
  
  public void setDiscardable(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_15, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.discardable = paramInt;
  }
  
  public void setFrameRate(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_21, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.frameRate = paramInt;
  }
  
  public void setLevelIndication(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.levelIndication = paramInt;
  }
  
  public void setProfileIndication(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.profileIndication = paramInt;
  }
  
  public void setProfile_compatibility(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.profile_compatibility = paramInt;
  }
  
  public void setReserved1(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reserved1 = paramInt;
  }
  
  public void setReserved2(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_19, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reserved2 = paramInt;
  }
  
  public void setTierID(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.tierID = paramInt;
  }
  
  public void setVisualHeight(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.visualHeight = paramInt;
  }
  
  public void setVisualWidth(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.visualWidth = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part15/TierInfoBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */