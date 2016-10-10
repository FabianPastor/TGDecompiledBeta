package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class HintMediaHeaderBox
  extends AbstractMediaHeaderBox
{
  public static final String TYPE = "hmhd";
  private long avgBitrate;
  private int avgPduSize;
  private long maxBitrate;
  private int maxPduSize;
  
  static {}
  
  public HintMediaHeaderBox()
  {
    super("hmhd");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.maxPduSize = IsoTypeReader.readUInt16(paramByteBuffer);
    this.avgPduSize = IsoTypeReader.readUInt16(paramByteBuffer);
    this.maxBitrate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.avgBitrate = IsoTypeReader.readUInt32(paramByteBuffer);
    IsoTypeReader.readUInt32(paramByteBuffer);
  }
  
  public long getAvgBitrate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avgBitrate;
  }
  
  public int getAvgPduSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avgPduSize;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.maxPduSize);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.avgPduSize);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.maxBitrate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.avgBitrate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
  }
  
  protected long getContentSize()
  {
    return 20L;
  }
  
  public long getMaxBitrate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.maxBitrate;
  }
  
  public int getMaxPduSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.maxPduSize;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "HintMediaHeaderBox{maxPduSize=" + this.maxPduSize + ", avgPduSize=" + this.avgPduSize + ", maxBitrate=" + this.maxBitrate + ", avgBitrate=" + this.avgBitrate + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/HintMediaHeaderBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */