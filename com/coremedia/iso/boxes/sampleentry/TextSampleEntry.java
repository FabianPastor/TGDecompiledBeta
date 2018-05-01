package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.DataSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

public class TextSampleEntry
  extends AbstractSampleEntry
{
  public static final String TYPE1 = "tx3g";
  public static final String TYPE_ENCRYPTED = "enct";
  private int[] backgroundColorRgba = new int[4];
  private BoxRecord boxRecord = new BoxRecord();
  private long displayFlags;
  private int horizontalJustification;
  private StyleRecord styleRecord = new StyleRecord();
  private int verticalJustification;
  
  public TextSampleEntry()
  {
    super("tx3g");
  }
  
  public TextSampleEntry(String paramString)
  {
    super(paramString);
  }
  
  public int[] getBackgroundColorRgba()
  {
    return this.backgroundColorRgba;
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    paramWritableByteChannel.write(getHeader());
    ByteBuffer localByteBuffer = ByteBuffer.allocate(38);
    localByteBuffer.position(6);
    IsoTypeWriter.writeUInt16(localByteBuffer, this.dataReferenceIndex);
    IsoTypeWriter.writeUInt32(localByteBuffer, this.displayFlags);
    IsoTypeWriter.writeUInt8(localByteBuffer, this.horizontalJustification);
    IsoTypeWriter.writeUInt8(localByteBuffer, this.verticalJustification);
    IsoTypeWriter.writeUInt8(localByteBuffer, this.backgroundColorRgba[0]);
    IsoTypeWriter.writeUInt8(localByteBuffer, this.backgroundColorRgba[1]);
    IsoTypeWriter.writeUInt8(localByteBuffer, this.backgroundColorRgba[2]);
    IsoTypeWriter.writeUInt8(localByteBuffer, this.backgroundColorRgba[3]);
    this.boxRecord.getContent(localByteBuffer);
    this.styleRecord.getContent(localByteBuffer);
    paramWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
    writeContainer(paramWritableByteChannel);
  }
  
  public BoxRecord getBoxRecord()
  {
    return this.boxRecord;
  }
  
  public int getHorizontalJustification()
  {
    return this.horizontalJustification;
  }
  
  public long getSize()
  {
    long l = getContainerSize();
    if ((this.largeBox) || (l + 38L >= 4294967296L)) {}
    for (int i = 16;; i = 8) {
      return i + (l + 38L);
    }
  }
  
  public StyleRecord getStyleRecord()
  {
    return this.styleRecord;
  }
  
  public int getVerticalJustification()
  {
    return this.verticalJustification;
  }
  
  public boolean isContinuousKaraoke()
  {
    if ((this.displayFlags & 0x800) == 2048L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isFillTextRegion()
  {
    if ((this.displayFlags & 0x40000) == 262144L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isScrollDirection()
  {
    if ((this.displayFlags & 0x180) == 384L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isScrollIn()
  {
    if ((this.displayFlags & 0x20) == 32L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isScrollOut()
  {
    if ((this.displayFlags & 0x40) == 64L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isWriteTextVertically()
  {
    if ((this.displayFlags & 0x20000) == 131072L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    paramByteBuffer = ByteBuffer.allocate(38);
    paramDataSource.read(paramByteBuffer);
    paramByteBuffer.position(6);
    this.dataReferenceIndex = IsoTypeReader.readUInt16(paramByteBuffer);
    this.displayFlags = IsoTypeReader.readUInt32(paramByteBuffer);
    this.horizontalJustification = IsoTypeReader.readUInt8(paramByteBuffer);
    this.verticalJustification = IsoTypeReader.readUInt8(paramByteBuffer);
    this.backgroundColorRgba = new int[4];
    this.backgroundColorRgba[0] = IsoTypeReader.readUInt8(paramByteBuffer);
    this.backgroundColorRgba[1] = IsoTypeReader.readUInt8(paramByteBuffer);
    this.backgroundColorRgba[2] = IsoTypeReader.readUInt8(paramByteBuffer);
    this.backgroundColorRgba[3] = IsoTypeReader.readUInt8(paramByteBuffer);
    this.boxRecord = new BoxRecord();
    this.boxRecord.parse(paramByteBuffer);
    this.styleRecord = new StyleRecord();
    this.styleRecord.parse(paramByteBuffer);
    initContainer(paramDataSource, paramLong - 38L, paramBoxParser);
  }
  
  public void setBackgroundColorRgba(int[] paramArrayOfInt)
  {
    this.backgroundColorRgba = paramArrayOfInt;
  }
  
  public void setBoxRecord(BoxRecord paramBoxRecord)
  {
    this.boxRecord = paramBoxRecord;
  }
  
  public void setContinuousKaraoke(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (this.displayFlags |= 0x800;; this.displayFlags &= 0xFFFFFFFFFFFFF7FF) {
      return;
    }
  }
  
  public void setFillTextRegion(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (this.displayFlags |= 0x40000;; this.displayFlags &= 0xFFFFFFFFFFFBFFFF) {
      return;
    }
  }
  
  public void setHorizontalJustification(int paramInt)
  {
    this.horizontalJustification = paramInt;
  }
  
  public void setScrollDirection(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (this.displayFlags |= 0x180;; this.displayFlags &= 0xFFFFFFFFFFFFFE7F) {
      return;
    }
  }
  
  public void setScrollIn(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (this.displayFlags |= 0x20;; this.displayFlags &= 0xFFFFFFFFFFFFFFDF) {
      return;
    }
  }
  
  public void setScrollOut(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (this.displayFlags |= 0x40;; this.displayFlags &= 0xFFFFFFFFFFFFFFBF) {
      return;
    }
  }
  
  public void setStyleRecord(StyleRecord paramStyleRecord)
  {
    this.styleRecord = paramStyleRecord;
  }
  
  public void setType(String paramString)
  {
    this.type = paramString;
  }
  
  public void setVerticalJustification(int paramInt)
  {
    this.verticalJustification = paramInt;
  }
  
  public void setWriteTextVertically(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (this.displayFlags |= 0x20000;; this.displayFlags &= 0xFFFFFFFFFFFDFFFF) {
      return;
    }
  }
  
  public String toString()
  {
    return "TextSampleEntry";
  }
  
  public static class BoxRecord
  {
    int bottom;
    int left;
    int right;
    int top;
    
    public BoxRecord() {}
    
    public BoxRecord(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.top = paramInt1;
      this.left = paramInt2;
      this.bottom = paramInt3;
      this.right = paramInt4;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this == paramObject) {}
      for (;;)
      {
        return bool;
        if ((paramObject == null) || (getClass() != paramObject.getClass()))
        {
          bool = false;
        }
        else
        {
          paramObject = (BoxRecord)paramObject;
          if (this.bottom != ((BoxRecord)paramObject).bottom) {
            bool = false;
          } else if (this.left != ((BoxRecord)paramObject).left) {
            bool = false;
          } else if (this.right != ((BoxRecord)paramObject).right) {
            bool = false;
          } else if (this.top != ((BoxRecord)paramObject).top) {
            bool = false;
          }
        }
      }
    }
    
    public void getContent(ByteBuffer paramByteBuffer)
    {
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.top);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.left);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.bottom);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.right);
    }
    
    public int getSize()
    {
      return 8;
    }
    
    public int hashCode()
    {
      return ((this.top * 31 + this.left) * 31 + this.bottom) * 31 + this.right;
    }
    
    public void parse(ByteBuffer paramByteBuffer)
    {
      this.top = IsoTypeReader.readUInt16(paramByteBuffer);
      this.left = IsoTypeReader.readUInt16(paramByteBuffer);
      this.bottom = IsoTypeReader.readUInt16(paramByteBuffer);
      this.right = IsoTypeReader.readUInt16(paramByteBuffer);
    }
  }
  
  public static class StyleRecord
  {
    int endChar;
    int faceStyleFlags;
    int fontId;
    int fontSize;
    int startChar;
    int[] textColor = { 255, 255, 255, 255 };
    
    public StyleRecord() {}
    
    public StyleRecord(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt)
    {
      this.startChar = paramInt1;
      this.endChar = paramInt2;
      this.fontId = paramInt3;
      this.faceStyleFlags = paramInt4;
      this.fontSize = paramInt5;
      this.textColor = paramArrayOfInt;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this == paramObject) {}
      for (;;)
      {
        return bool;
        if ((paramObject == null) || (getClass() != paramObject.getClass()))
        {
          bool = false;
        }
        else
        {
          paramObject = (StyleRecord)paramObject;
          if (this.endChar != ((StyleRecord)paramObject).endChar) {
            bool = false;
          } else if (this.faceStyleFlags != ((StyleRecord)paramObject).faceStyleFlags) {
            bool = false;
          } else if (this.fontId != ((StyleRecord)paramObject).fontId) {
            bool = false;
          } else if (this.fontSize != ((StyleRecord)paramObject).fontSize) {
            bool = false;
          } else if (this.startChar != ((StyleRecord)paramObject).startChar) {
            bool = false;
          } else if (!Arrays.equals(this.textColor, ((StyleRecord)paramObject).textColor)) {
            bool = false;
          }
        }
      }
    }
    
    public void getContent(ByteBuffer paramByteBuffer)
    {
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.startChar);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.endChar);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.fontId);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.faceStyleFlags);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.fontSize);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.textColor[0]);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.textColor[1]);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.textColor[2]);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.textColor[3]);
    }
    
    public int getSize()
    {
      return 12;
    }
    
    public int hashCode()
    {
      int i = this.startChar;
      int j = this.endChar;
      int k = this.fontId;
      int m = this.faceStyleFlags;
      int n = this.fontSize;
      if (this.textColor != null) {}
      for (int i1 = Arrays.hashCode(this.textColor);; i1 = 0) {
        return ((((i * 31 + j) * 31 + k) * 31 + m) * 31 + n) * 31 + i1;
      }
    }
    
    public void parse(ByteBuffer paramByteBuffer)
    {
      this.startChar = IsoTypeReader.readUInt16(paramByteBuffer);
      this.endChar = IsoTypeReader.readUInt16(paramByteBuffer);
      this.fontId = IsoTypeReader.readUInt16(paramByteBuffer);
      this.faceStyleFlags = IsoTypeReader.readUInt8(paramByteBuffer);
      this.fontSize = IsoTypeReader.readUInt8(paramByteBuffer);
      this.textColor = new int[4];
      this.textColor[0] = IsoTypeReader.readUInt8(paramByteBuffer);
      this.textColor[1] = IsoTypeReader.readUInt8(paramByteBuffer);
      this.textColor[2] = IsoTypeReader.readUInt8(paramByteBuffer);
      this.textColor[3] = IsoTypeReader.readUInt8(paramByteBuffer);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/sampleentry/TextSampleEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */