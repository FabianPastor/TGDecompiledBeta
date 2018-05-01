package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class VideoMediaHeaderBox
  extends AbstractMediaHeaderBox
{
  public static final String TYPE = "vmhd";
  private int graphicsmode = 0;
  private int[] opcolor = new int[3];
  
  static {}
  
  public VideoMediaHeaderBox()
  {
    super("vmhd");
    setFlags(1);
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.graphicsmode = IsoTypeReader.readUInt16(paramByteBuffer);
    this.opcolor = new int[3];
    for (int i = 0;; i++)
    {
      if (i >= 3) {
        return;
      }
      this.opcolor[i] = IsoTypeReader.readUInt16(paramByteBuffer);
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.graphicsmode);
    int[] arrayOfInt = this.opcolor;
    int i = arrayOfInt.length;
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      IsoTypeWriter.writeUInt16(paramByteBuffer, arrayOfInt[j]);
    }
  }
  
  protected long getContentSize()
  {
    return 12L;
  }
  
  public int getGraphicsmode()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.graphicsmode;
  }
  
  public int[] getOpcolor()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.opcolor;
  }
  
  public void setGraphicsmode(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.graphicsmode = paramInt;
  }
  
  public void setOpcolor(int[] paramArrayOfInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramArrayOfInt);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.opcolor = paramArrayOfInt;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "VideoMediaHeaderBox[graphicsmode=" + getGraphicsmode() + ";opcolor0=" + getOpcolor()[0] + ";opcolor1=" + getOpcolor()[1] + ";opcolor2=" + getOpcolor()[2] + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/VideoMediaHeaderBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */