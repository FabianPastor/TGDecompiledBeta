package org.telegram.messenger.audioinfo.mp3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import org.telegram.messenger.audioinfo.util.RangeInputStream;

public class ID3v2TagBody
{
  private final ID3v2DataInput data;
  private final RangeInputStream input;
  private final ID3v2TagHeader tagHeader;
  
  ID3v2TagBody(InputStream paramInputStream, long paramLong, int paramInt, ID3v2TagHeader paramID3v2TagHeader)
    throws IOException
  {
    this.input = new RangeInputStream(paramInputStream, paramLong, paramInt);
    this.data = new ID3v2DataInput(this.input);
    this.tagHeader = paramID3v2TagHeader;
  }
  
  public ID3v2FrameBody frameBody(ID3v2FrameHeader paramID3v2FrameHeader)
    throws IOException, ID3v2Exception
  {
    int j = paramID3v2FrameHeader.getBodySize();
    Object localObject1 = this.input;
    int k;
    int m;
    int i;
    if (paramID3v2FrameHeader.isUnsynchronization())
    {
      localObject1 = this.data.readFully(paramID3v2FrameHeader.getBodySize());
      k = 0;
      int n = localObject1.length;
      m = 0;
      j = 0;
      if (m < n)
      {
        i = localObject1[m];
        if ((k != 0) && (i == 0)) {
          break label188;
        }
        k = j + 1;
        localObject1[j] = i;
        j = k;
      }
    }
    label188:
    for (;;)
    {
      if (i == 255) {}
      for (k = 1;; k = 0)
      {
        m += 1;
        break;
      }
      k = j;
      localObject1 = new ByteArrayInputStream((byte[])localObject1, 0, j);
      j = k;
      if (paramID3v2FrameHeader.isEncryption()) {
        throw new ID3v2Exception("Frame encryption is not supported");
      }
      Object localObject2 = localObject1;
      if (paramID3v2FrameHeader.isCompression())
      {
        j = paramID3v2FrameHeader.getDataLengthIndicator();
        localObject2 = new InflaterInputStream((InputStream)localObject1);
      }
      return new ID3v2FrameBody((InputStream)localObject2, paramID3v2FrameHeader.getHeaderSize(), j, this.tagHeader, paramID3v2FrameHeader);
    }
  }
  
  public ID3v2DataInput getData()
  {
    return this.data;
  }
  
  public long getPosition()
  {
    return this.input.getPosition();
  }
  
  public long getRemainingLength()
  {
    return this.input.getRemainingLength();
  }
  
  public ID3v2TagHeader getTagHeader()
  {
    return this.tagHeader;
  }
  
  public String toString()
  {
    return "id3v2tag[pos=" + getPosition() + ", " + getRemainingLength() + " left]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/mp3/ID3v2TagBody.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */