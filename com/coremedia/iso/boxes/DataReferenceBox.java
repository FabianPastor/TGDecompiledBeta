package com.coremedia.iso.boxes;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.DataSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.List;

public class DataReferenceBox
  extends AbstractContainerBox
  implements FullBox
{
  public static final String TYPE = "dref";
  private int flags;
  private int version;
  
  public DataReferenceBox()
  {
    super("dref");
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    paramWritableByteChannel.write(getHeader());
    ByteBuffer localByteBuffer = ByteBuffer.allocate(8);
    IsoTypeWriter.writeUInt8(localByteBuffer, this.version);
    IsoTypeWriter.writeUInt24(localByteBuffer, this.flags);
    IsoTypeWriter.writeUInt32(localByteBuffer, getBoxes().size());
    paramWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
    writeContainer(paramWritableByteChannel);
  }
  
  public int getFlags()
  {
    return this.flags;
  }
  
  public long getSize()
  {
    long l = getContainerSize();
    if ((this.largeBox) || (l + 8L + 8L >= 4294967296L)) {}
    for (int i = 16;; i = 8) {
      return i + (l + 8L);
    }
  }
  
  public int getVersion()
  {
    return this.version;
  }
  
  public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    paramByteBuffer = ByteBuffer.allocate(8);
    paramDataSource.read(paramByteBuffer);
    paramByteBuffer.rewind();
    this.version = IsoTypeReader.readUInt8(paramByteBuffer);
    this.flags = IsoTypeReader.readUInt24(paramByteBuffer);
    initContainer(paramDataSource, paramLong - 8L, paramBoxParser);
  }
  
  public void setFlags(int paramInt)
  {
    this.flags = paramInt;
  }
  
  public void setVersion(int paramInt)
  {
    this.version = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/DataReferenceBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */