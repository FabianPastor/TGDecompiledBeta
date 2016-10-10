package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class TrackReferenceTypeBox
  extends AbstractBox
{
  public static final String TYPE1 = "hint";
  public static final String TYPE2 = "cdsc";
  private long[] trackIds;
  
  static {}
  
  public TrackReferenceTypeBox(String paramString)
  {
    super(paramString);
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    int j = paramByteBuffer.remaining() / 4;
    this.trackIds = new long[j];
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      this.trackIds[i] = IsoTypeReader.readUInt32(paramByteBuffer);
      i += 1;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    long[] arrayOfLong = this.trackIds;
    int j = arrayOfLong.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      IsoTypeWriter.writeUInt32(paramByteBuffer, arrayOfLong[i]);
      i += 1;
    }
  }
  
  protected long getContentSize()
  {
    return this.trackIds.length * 4;
  }
  
  public long[] getTrackIds()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.trackIds;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("TrackReferenceTypeBox[type=").append(getType());
    int i = 0;
    for (;;)
    {
      if (i >= this.trackIds.length)
      {
        ((StringBuilder)localObject).append("]");
        return ((StringBuilder)localObject).toString();
      }
      ((StringBuilder)localObject).append(";trackId");
      ((StringBuilder)localObject).append(i);
      ((StringBuilder)localObject).append("=");
      ((StringBuilder)localObject).append(this.trackIds[i]);
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/TrackReferenceTypeBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */