package com.mp4parser.iso14496.part30;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry;
import com.googlecode.mp4parser.DataSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class XMLSubtitleSampleEntry
  extends AbstractSampleEntry
{
  public static final String TYPE = "stpp";
  private String auxiliaryMimeTypes = "";
  private String namespace = "";
  private String schemaLocation = "";
  
  public XMLSubtitleSampleEntry()
  {
    super("stpp");
  }
  
  public String getAuxiliaryMimeTypes()
  {
    return this.auxiliaryMimeTypes;
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    paramWritableByteChannel.write(getHeader());
    ByteBuffer localByteBuffer = ByteBuffer.allocate(this.namespace.length() + 8 + this.schemaLocation.length() + this.auxiliaryMimeTypes.length() + 3);
    localByteBuffer.position(6);
    IsoTypeWriter.writeUInt16(localByteBuffer, this.dataReferenceIndex);
    IsoTypeWriter.writeZeroTermUtf8String(localByteBuffer, this.namespace);
    IsoTypeWriter.writeZeroTermUtf8String(localByteBuffer, this.schemaLocation);
    IsoTypeWriter.writeZeroTermUtf8String(localByteBuffer, this.auxiliaryMimeTypes);
    paramWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
    writeContainer(paramWritableByteChannel);
  }
  
  public String getNamespace()
  {
    return this.namespace;
  }
  
  public String getSchemaLocation()
  {
    return this.schemaLocation;
  }
  
  public long getSize()
  {
    long l1 = getContainerSize();
    long l2 = this.namespace.length() + 8 + this.schemaLocation.length() + this.auxiliaryMimeTypes.length() + 3;
    if ((this.largeBox) || (l1 + l2 + 8L >= 4294967296L)) {}
    for (int i = 16;; i = 8) {
      return i + (l1 + l2);
    }
  }
  
  public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(8);
    paramDataSource.read((ByteBuffer)localByteBuffer.rewind());
    localByteBuffer.position(6);
    this.dataReferenceIndex = IsoTypeReader.readUInt16(localByteBuffer);
    long l = paramDataSource.position();
    localByteBuffer = ByteBuffer.allocate(1024);
    paramDataSource.read((ByteBuffer)localByteBuffer.rewind());
    this.namespace = IsoTypeReader.readString((ByteBuffer)localByteBuffer.rewind());
    paramDataSource.position(this.namespace.length() + l + 1L);
    paramDataSource.read((ByteBuffer)localByteBuffer.rewind());
    this.schemaLocation = IsoTypeReader.readString((ByteBuffer)localByteBuffer.rewind());
    paramDataSource.position(this.namespace.length() + l + this.schemaLocation.length() + 2L);
    paramDataSource.read((ByteBuffer)localByteBuffer.rewind());
    this.auxiliaryMimeTypes = IsoTypeReader.readString((ByteBuffer)localByteBuffer.rewind());
    paramDataSource.position(this.namespace.length() + l + this.schemaLocation.length() + this.auxiliaryMimeTypes.length() + 3L);
    initContainer(paramDataSource, paramLong - (paramByteBuffer.remaining() + this.namespace.length() + this.schemaLocation.length() + this.auxiliaryMimeTypes.length() + 3), paramBoxParser);
  }
  
  public void setAuxiliaryMimeTypes(String paramString)
  {
    this.auxiliaryMimeTypes = paramString;
  }
  
  public void setNamespace(String paramString)
  {
    this.namespace = paramString;
  }
  
  public void setSchemaLocation(String paramString)
  {
    this.schemaLocation = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part30/XMLSubtitleSampleEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */