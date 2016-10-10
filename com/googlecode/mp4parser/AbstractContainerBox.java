package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class AbstractContainerBox
  extends BasicContainer
  implements Box
{
  protected boolean largeBox;
  private long offset;
  Container parent;
  protected String type;
  
  public AbstractContainerBox(String paramString)
  {
    this.type = paramString;
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    paramWritableByteChannel.write(getHeader());
    writeContainer(paramWritableByteChannel);
  }
  
  protected ByteBuffer getHeader()
  {
    Object localObject;
    if ((this.largeBox) || (getSize() >= 4294967296L))
    {
      localObject = new byte[16];
      localObject[3] = 1;
      localObject[4] = this.type.getBytes()[0];
      localObject[5] = this.type.getBytes()[1];
      localObject[6] = this.type.getBytes()[2];
      localObject[7] = this.type.getBytes()[3];
      localObject = ByteBuffer.wrap((byte[])localObject);
      ((ByteBuffer)localObject).position(8);
      IsoTypeWriter.writeUInt64((ByteBuffer)localObject, getSize());
    }
    for (;;)
    {
      ((ByteBuffer)localObject).rewind();
      return (ByteBuffer)localObject;
      localObject = new byte[8];
      localObject[4] = this.type.getBytes()[0];
      localObject[5] = this.type.getBytes()[1];
      localObject[6] = this.type.getBytes()[2];
      localObject[7] = this.type.getBytes()[3];
      localObject = ByteBuffer.wrap((byte[])localObject);
      IsoTypeWriter.writeUInt32((ByteBuffer)localObject, getSize());
    }
  }
  
  public long getOffset()
  {
    return this.offset;
  }
  
  public Container getParent()
  {
    return this.parent;
  }
  
  public long getSize()
  {
    long l = getContainerSize();
    if ((this.largeBox) || (8L + l >= 4294967296L)) {}
    for (int i = 16;; i = 8) {
      return i + l;
    }
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public void initContainer(DataSource paramDataSource, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    this.dataSource = paramDataSource;
    this.parsePosition = paramDataSource.position();
    long l = this.parsePosition;
    if ((this.largeBox) || (8L + paramLong >= 4294967296L)) {}
    for (int i = 16;; i = 8)
    {
      this.startPosition = (l - i);
      paramDataSource.position(paramDataSource.position() + paramLong);
      this.endPosition = paramDataSource.position();
      this.boxParser = paramBoxParser;
      return;
    }
  }
  
  public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    this.offset = (paramDataSource.position() - paramByteBuffer.remaining());
    if (paramByteBuffer.remaining() == 16) {}
    for (boolean bool = true;; bool = false)
    {
      this.largeBox = bool;
      initContainer(paramDataSource, paramLong, paramBoxParser);
      return;
    }
  }
  
  public void setParent(Container paramContainer)
  {
    this.parent = paramContainer;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/AbstractContainerBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */