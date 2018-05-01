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
          break label257;
        }
        bool1 = bool2;
        label234:
        this.compression = bool1;
      }
    }
    label257:
    label271:
    label372:
    label386:
    for (;;)
    {
      this.headerSize = ((int)(paramPositionInputStream.getPosition() - l));
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label234;
      if ((i & 0x80) != 0)
      {
        this.unsynchronization = bool1;
        if ((i & 0x40) != 0)
        {
          if (this.version != 3) {
            break label372;
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
          break label386;
        }
        this.footerSize = 10;
        this.totalTagSize += 10;
        break;
        bool1 = false;
        break label271;
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
    int j;
    if ((this.version < 4) && (this.unsynchronization))
    {
      paramInputStream = new ID3v2DataInput(paramInputStream).readFully(this.totalTagSize - this.headerSize);
      int i = 0;
      j = 0;
      int k = paramInputStream.length;
      int m = 0;
      if (m < k)
      {
        int n = paramInputStream[m];
        int i1;
        if (i != 0)
        {
          i1 = j;
          if (n == 0) {}
        }
        else
        {
          paramInputStream[j] = ((byte)n);
          i1 = j + 1;
        }
        if (n == -1) {}
        for (i = 1;; i = 0)
        {
          m++;
          j = i1;
          break;
        }
      }
    }
    for (paramInputStream = new ID3v2TagBody(new ByteArrayInputStream(paramInputStream, 0, j), this.headerSize, j, this);; paramInputStream = new ID3v2TagBody(paramInputStream, this.headerSize, this.totalTagSize - this.headerSize - this.footerSize, this)) {
      return paramInputStream;
    }
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