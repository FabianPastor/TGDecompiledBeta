package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class BaseMediaInfoAtom
  extends AbstractFullBox
{
  public static final String TYPE = "gmin";
  short balance;
  short graphicsMode = 64;
  int opColorB = 32768;
  int opColorG = 32768;
  int opColorR = 32768;
  short reserved;
  
  static {}
  
  public BaseMediaInfoAtom()
  {
    super("gmin");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.graphicsMode = paramByteBuffer.getShort();
    this.opColorR = IsoTypeReader.readUInt16(paramByteBuffer);
    this.opColorG = IsoTypeReader.readUInt16(paramByteBuffer);
    this.opColorB = IsoTypeReader.readUInt16(paramByteBuffer);
    this.balance = paramByteBuffer.getShort();
    this.reserved = paramByteBuffer.getShort();
  }
  
  public short getBalance()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.balance;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.putShort(this.graphicsMode);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.opColorR);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.opColorG);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.opColorB);
    paramByteBuffer.putShort(this.balance);
    paramByteBuffer.putShort(this.reserved);
  }
  
  protected long getContentSize()
  {
    return 16L;
  }
  
  public short getGraphicsMode()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.graphicsMode;
  }
  
  public int getOpColorB()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.opColorB;
  }
  
  public int getOpColorG()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.opColorG;
  }
  
  public int getOpColorR()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.opColorR;
  }
  
  public short getReserved()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved;
  }
  
  public void setBalance(short paramShort)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.shortObject(paramShort));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.balance = paramShort;
  }
  
  public void setGraphicsMode(short paramShort)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.shortObject(paramShort));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.graphicsMode = paramShort;
  }
  
  public void setOpColorB(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.opColorB = paramInt;
  }
  
  public void setOpColorG(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.opColorG = paramInt;
  }
  
  public void setOpColorR(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.opColorR = paramInt;
  }
  
  public void setReserved(short paramShort)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.shortObject(paramShort));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reserved = paramShort;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "BaseMediaInfoAtom{graphicsMode=" + this.graphicsMode + ", opColorR=" + this.opColorR + ", opColorG=" + this.opColorG + ", opColorB=" + this.opColorB + ", balance=" + this.balance + ", reserved=" + this.reserved + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/BaseMediaInfoAtom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */