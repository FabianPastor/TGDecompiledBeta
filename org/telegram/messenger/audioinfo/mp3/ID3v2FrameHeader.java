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
    label96:
    int i1;
    int n;
    int j;
    int i;
    int k;
    int m;
    label148:
    boolean bool;
    if (paramID3v2TagBody.getTagHeader().getVersion() == 2)
    {
      this.frameId = new String(localID3v2DataInput.readFully(3), "ISO-8859-1");
      if (paramID3v2TagBody.getTagHeader().getVersion() != 2) {
        break label317;
      }
      this.bodySize = ((localID3v2DataInput.readByte() & 0xFF) << 16 | (localID3v2DataInput.readByte() & 0xFF) << 8 | localID3v2DataInput.readByte() & 0xFF);
      if (paramID3v2TagBody.getTagHeader().getVersion() > 2)
      {
        localID3v2DataInput.readByte();
        i1 = localID3v2DataInput.readByte();
        n = 0;
        j = 0;
        if (paramID3v2TagBody.getTagHeader().getVersion() != 3) {
          break label352;
        }
        i = 128;
        k = 64;
        m = 32;
        if ((i1 & i) == 0) {
          break label370;
        }
        bool = true;
        label158:
        this.compression = bool;
        if ((i1 & n) == 0) {
          break label376;
        }
        bool = true;
        label175:
        this.unsynchronization = bool;
        if ((i1 & k) == 0) {
          break label382;
        }
        bool = true;
        label192:
        this.encryption = bool;
        if (paramID3v2TagBody.getTagHeader().getVersion() != 3) {
          break label388;
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
        if ((i1 & m) != 0)
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
      label317:
      if (paramID3v2TagBody.getTagHeader().getVersion() == 3)
      {
        this.bodySize = localID3v2DataInput.readInt();
        break label96;
      }
      this.bodySize = localID3v2DataInput.readSyncsafeInt();
      break label96;
      label352:
      m = 64;
      i = 8;
      k = 4;
      n = 2;
      j = 1;
      break label148;
      label370:
      bool = false;
      break label158;
      label376:
      bool = false;
      break label175;
      label382:
      bool = false;
      break label192;
      label388:
      if ((i1 & m) != 0)
      {
        localID3v2DataInput.readByte();
        this.bodySize -= 1;
      }
      if (this.encryption)
      {
        localID3v2DataInput.readByte();
        this.bodySize -= 1;
      }
      if ((i1 & j) != 0)
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
    int i = 0;
    if (i < this.frameId.length()) {
      if (this.frameId.charAt(0) == 0) {}
    }
    while (this.bodySize != 0)
    {
      return false;
      i += 1;
      break;
    }
    return true;
  }
  
  public boolean isUnsynchronization()
  {
    return this.unsynchronization;
  }
  
  public boolean isValid()
  {
    int i = 0;
    if (i < this.frameId.length()) {
      if (((this.frameId.charAt(i) >= 'A') && (this.frameId.charAt(i) <= 'Z')) || ((this.frameId.charAt(i) >= '0') && (this.frameId.charAt(i) <= '9'))) {}
    }
    while (this.bodySize <= 0)
    {
      return false;
      i += 1;
      break;
    }
    return true;
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