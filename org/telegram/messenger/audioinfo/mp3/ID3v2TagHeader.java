package org.telegram.messenger.audioinfo.mp3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.PositionInputStream;

public class ID3v2TagHeader
{
  private boolean compression;
  private int footerSize = 0;
  private int headerSize = 0;
  private int paddingSize = 0;
  private int revision = 0;
  private int totalTagSize = 0;
  private boolean unsynchronization;
  private int version = 0;
  
  public ID3v2TagHeader(InputStream paramInputStream)
    throws IOException, ID3v2Exception
  {
    this(new PositionInputStream(paramInputStream));
  }
  
  ID3v2TagHeader(PositionInputStream paramPositionInputStream)
    throws IOException, ID3v2Exception
  {
    long l = paramPositionInputStream.getPosition();
    ID3v2DataInput localID3v2DataInput = new ID3v2DataInput(paramPositionInputStream);
    String str = new String(localID3v2DataInput.readFully(3), "ISO-8859-1");
    if (!"ID3".equals(str)) {
      throw new ID3v2Exception("Invalid ID3 identifier: " + str);
    }
    this.version = localID3v2DataInput.readByte();
    if ((this.version != 2) && (this.version != 3) && (this.version != 4)) {
      throw new ID3v2Exception("Unsupported ID3v2 version: " + this.version);
    }
    this.revision = localID3v2DataInput.readByte();
    int i = localID3v2DataInput.readByte();
    this.totalTagSize = (localID3v2DataInput.readSyncsafeInt() + 10);
    if (this.version == 2) {
      if ((i & 0x80) != 0)
      {
        bool1 = true;
        this.unsynchronization = bool1;
        if ((i & 0x40) == 0) {
          break label262;
        }
        bool1 = bool2;
        label237:
        this.compression = bool1;
      }
    }
    label262:
    label276:
    label375:
    label389:
    for (;;)
    {
      this.headerSize = ((int)(paramPositionInputStream.getPosition() - l));
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label237;
      if ((i & 0x80) != 0)
      {
        this.unsynchronization = bool1;
        if ((i & 0x40) != 0)
        {
          if (this.version != 3) {
            break label375;
          }
          int j = localID3v2DataInput.readInt();
          localID3v2DataInput.readByte();
          localID3v2DataInput.readByte();
          this.paddingSize = localID3v2DataInput.readInt();
          localID3v2DataInput.skipFully(j - 6);
        }
      }
      for (;;)
      {
        if ((this.version < 4) || ((i & 0x10) == 0)) {
          break label389;
        }
        this.footerSize = 10;
        this.totalTagSize += 10;
        break;
        bool1 = false;
        break label276;
        localID3v2DataInput.skipFully(localID3v2DataInput.readSyncsafeInt() - 4);
      }
    }
  }
  
  public int getFooterSize()
  {
    return this.footerSize;
  }
  
  public int getHeaderSize()
  {
    return this.headerSize;
  }
  
  public int getPaddingSize()
  {
    return this.paddingSize;
  }
  
  public int getRevision()
  {
    return this.revision;
  }
  
  public int getTotalTagSize()
  {
    return this.totalTagSize;
  }
  
  public int getVersion()
  {
    return this.version;
  }
  
  public boolean isCompression()
  {
    return this.compression;
  }
  
  public boolean isUnsynchronization()
  {
    return this.unsynchronization;
  }
  
  public ID3v2TagBody tagBody(InputStream paramInputStream)
    throws IOException, ID3v2Exception
  {
    if (this.compression) {
      throw new ID3v2Exception("Tag compression is not supported");
    }
    if ((this.version < 4) && (this.unsynchronization))
    {
      paramInputStream = new ID3v2DataInput(paramInputStream).readFully(this.totalTagSize - this.headerSize);
      int j = 0;
      int n = 0;
      int i1 = paramInputStream.length;
      int k = 0;
      if (k < i1)
      {
        int i = paramInputStream[k];
        int m;
        if (j != 0)
        {
          m = n;
          if (i == 0) {}
        }
        else
        {
          paramInputStream[n] = i;
          m = n + 1;
        }
        if (i == 255) {}
        for (j = 1;; j = 0)
        {
          k += 1;
          n = m;
          break;
        }
      }
      return new ID3v2TagBody(new ByteArrayInputStream(paramInputStream, 0, n), this.headerSize, n, this);
    }
    return new ID3v2TagBody(paramInputStream, this.headerSize, this.totalTagSize - this.headerSize - this.footerSize, this);
  }
  
  public String toString()
  {
    return String.format("%s[version=%s, totalTagSize=%d]", new Object[] { getClass().getSimpleName(), Integer.valueOf(this.version), Integer.valueOf(this.totalTagSize) });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/mp3/ID3v2TagHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */