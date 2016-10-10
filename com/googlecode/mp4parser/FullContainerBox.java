package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.FullBox;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.logging.Logger;

public abstract class FullContainerBox
  extends AbstractContainerBox
  implements FullBox
{
  private static Logger LOG = Logger.getLogger(FullContainerBox.class.getName());
  private int flags;
  private int version;
  
  public FullContainerBox(String paramString)
  {
    super(paramString);
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    super.getBox(paramWritableByteChannel);
  }
  
  public <T extends Box> List<T> getBoxes(Class<T> paramClass)
  {
    return getBoxes(paramClass, false);
  }
  
  public int getFlags()
  {
    return this.flags;
  }
  
  protected ByteBuffer getHeader()
  {
    Object localObject;
    if ((this.largeBox) || (getSize() >= 4294967296L))
    {
      localObject = new byte[20];
      localObject[3] = 1;
      localObject[4] = this.type.getBytes()[0];
      localObject[5] = this.type.getBytes()[1];
      localObject[6] = this.type.getBytes()[2];
      localObject[7] = this.type.getBytes()[3];
      localObject = ByteBuffer.wrap((byte[])localObject);
      ((ByteBuffer)localObject).position(8);
      IsoTypeWriter.writeUInt64((ByteBuffer)localObject, getSize());
      writeVersionAndFlags((ByteBuffer)localObject);
    }
    for (;;)
    {
      ((ByteBuffer)localObject).rewind();
      return (ByteBuffer)localObject;
      localObject = new byte[12];
      localObject[4] = this.type.getBytes()[0];
      localObject[5] = this.type.getBytes()[1];
      localObject[6] = this.type.getBytes()[2];
      localObject[7] = this.type.getBytes()[3];
      localObject = ByteBuffer.wrap((byte[])localObject);
      IsoTypeWriter.writeUInt32((ByteBuffer)localObject, getSize());
      ((ByteBuffer)localObject).position(8);
      writeVersionAndFlags((ByteBuffer)localObject);
    }
  }
  
  public int getVersion()
  {
    return this.version;
  }
  
  public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(4);
    paramDataSource.read(localByteBuffer);
    parseVersionAndFlags((ByteBuffer)localByteBuffer.rewind());
    super.parse(paramDataSource, paramByteBuffer, paramLong, paramBoxParser);
  }
  
  protected final long parseVersionAndFlags(ByteBuffer paramByteBuffer)
  {
    this.version = IsoTypeReader.readUInt8(paramByteBuffer);
    this.flags = IsoTypeReader.readUInt24(paramByteBuffer);
    return 4L;
  }
  
  public void setFlags(int paramInt)
  {
    this.flags = paramInt;
  }
  
  public void setVersion(int paramInt)
  {
    this.version = paramInt;
  }
  
  public String toString()
  {
    return getClass().getSimpleName() + "[childBoxes]";
  }
  
  protected final void writeVersionAndFlags(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.version);
    IsoTypeWriter.writeUInt24(paramByteBuffer, this.flags);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/FullContainerBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */