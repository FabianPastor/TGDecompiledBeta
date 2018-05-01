package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;

public class ID3v2FrameHeader
{
  private int bodySize;
  private boolean compression;
  private int dataLengthIndicator;
  private boolean encryption;
  private String frameId;
  private int headerSize;
  private boolean unsynchronization;
  
  public ID3v2FrameHeader(ID3v2TagBody paramID3v2TagBody)
    throws IOException, ID3v2Exception
  {
    long l = paramID3v2TagBody.getPosition();
    ID3v2DataInput localID3v2DataInput = paramID3v2TagBody.getData();
    label95:
    int i;
    int j;
    int k;
    int m;
    int n;
    int i1;
    label149:
    boolean bool;
    if (paramID3v2TagBody.getTagHeader().getVersion() == 2)
    {
      this.frameId = new String(localID3v2DataInput.readFully(3), "ISO-8859-1");
      if (paramID3v2TagBody.getTagHeader().getVersion() != 2) {
        break label318;
      }
      this.bodySize = ((localID3v2DataInput.readByte() & 0xFF) << 16 | (localID3v2DataInput.readByte() & 0xFF) << 8 | localID3v2DataInput.readByte() & 0xFF);
      if (paramID3v2TagBody.getTagHeader().getVersion() > 2)
      {
        localID3v2DataInput.readByte();
        i = localID3v2DataInput.readByte();
        j = 0;
        k = 0;
        if (paramID3v2TagBody.getTagHeader().getVersion() != 3) {
          break label353;
        }
        m = 128;
        n = 64;
        i1 = 32;
        if ((i & m) == 0) {
          break label373;
        }
        bool = true;
        label160:
        this.compression = bool;
        if ((i & j) == 0) {
          break label379;
        }
        bool = true;
        label177:
        this.unsynchronization = bool;
        if ((i & n) == 0) {
          break label385;
        }
        bool = true;
        label194:
        this.encryption = bool;
        if (paramID3v2TagBody.getTagHeader().getVersion() != 3) {
          break label391;
        }
        if (this.compression)
        {
          this.dataLengthIndicator = localID3v2DataInput.readInt();
          this.bodySize -= 4;
        }
        if (this.encryption)
        {
          localID3v2DataInput.readByte();
          this.bodySize -= 1;
        }
        if ((i & i1) != 0)
        {
          localID3v2DataInput.readByte();
          this.bodySize -= 1;
        }
      }
    }
    for (;;)
    {
      this.headerSize = ((int)(paramID3v2TagBody.getPosition() - l));
      return;
      this.frameId = new String(localID3v2DataInput.readFully(4), "ISO-8859-1");
      break;
      label318:
      if (paramID3v2TagBody.getTagHeader().getVersion() == 3)
      {
        this.bodySize = localID3v2DataInput.readInt();
        break label95;
      }
      this.bodySize = localID3v2DataInput.readSyncsafeInt();
      break label95;
      label353:
      i1 = 64;
      m = 8;
      n = 4;
      j = 2;
      k = 1;
      break label149;
      label373:
      bool = false;
      break label160;
      label379:
      bool = false;
      break label177;
      label385:
      bool = false;
      break label194;
      label391:
      if ((i & i1) != 0)
      {
        localID3v2DataInput.readByte();
        this.bodySize -= 1;
      }
      if (this.encryption)
      {
        localID3v2DataInput.readByte();
        this.bodySize -= 1;
      }
      if ((i & k) != 0)
      {
        this.dataLengthIndicator = localID3v2DataInput.readSyncsafeInt();
        this.bodySize -= 4;
      }
    }
  }
  
  public int getBodySize()
  {
    return this.bodySize;
  }
  
  public int getDataLengthIndicator()
  {
    return this.dataLengthIndicator;
  }
  
  public String getFrameId()
  {
    return this.frameId;
  }
  
  public int getHeaderSize()
  {
    return this.headerSize;
  }
  
  public boolean isCompression()
  {
    return this.compression;
  }
  
  public boolean isEncryption()
  {
    return this.encryption;
  }
  
  public boolean isPadding()
  {
    boolean bool = false;
    int i = 0;
    if (i < this.frameId.length()) {
      if (this.frameId.charAt(0) == 0) {}
    }
    for (;;)
    {
      return bool;
      i++;
      break;
      if (this.bodySize == 0) {
        bool = true;
      }
    }
  }
  
  public boolean isUnsynchronization()
  {
    return this.unsynchronization;
  }
  
  public boolean isValid()
  {
    boolean bool1 = false;
    int i = 0;
    boolean bool2;
    if (i < this.frameId.length()) {
      if ((this.frameId.charAt(i) < 'A') || (this.frameId.charAt(i) > 'Z'))
      {
        bool2 = bool1;
        if (this.frameId.charAt(i) >= '0')
        {
          if (this.frameId.charAt(i) <= '9') {
            break label73;
          }
          bool2 = bool1;
        }
      }
    }
    for (;;)
    {
      return bool2;
      label73:
      i++;
      break;
      bool2 = bool1;
      if (this.bodySize > 0) {
        bool2 = true;
      }
    }
  }
  
  public String toString()
  {
    return String.format("%s[id=%s, bodysize=%d]", new Object[] { getClass().getSimpleName(), this.frameId, Integer.valueOf(this.bodySize) });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/mp3/ID3v2FrameHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */