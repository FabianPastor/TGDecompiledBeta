package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class SoundMediaHeaderBox
  extends AbstractMediaHeaderBox
{
  public static final String TYPE = "smhd";
  private float balance;
  
  static {}
  
  public SoundMediaHeaderBox()
  {
    super("smhd");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.balance = IsoTypeReader.readFixedPoint88(paramByteBuffer);
    IsoTypeReader.readUInt16(paramByteBuffer);
  }
  
  public float getBalance()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.balance;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeFixedPoint88(paramByteBuffer, this.balance);
    IsoTypeWriter.writeUInt16(paramByteBuffer, 0);
  }
  
  protected long getContentSize()
  {
    return 8L;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "SoundMediaHeaderBox[balance=" + getBalance() + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/SoundMediaHeaderBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */