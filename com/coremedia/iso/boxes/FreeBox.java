package com.coremedia.iso.boxes;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FreeBox
  implements Box
{
  public static final String TYPE = "free";
  ByteBuffer data;
  private long offset;
  private Container parent;
  List<Box> replacers = new LinkedList();
  
  static
  {
    if (!FreeBox.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public FreeBox()
  {
    this.data = ByteBuffer.wrap(new byte[0]);
  }
  
  public FreeBox(int paramInt)
  {
    this.data = ByteBuffer.allocate(paramInt);
  }
  
  public void addAndReplace(Box paramBox)
  {
    this.data.position(CastUtils.l2i(paramBox.getSize()));
    this.data = this.data.slice();
    this.replacers.add(paramBox);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (FreeBox)paramObject;
      if (getData() == null) {
        break;
      }
    } while (getData().equals(((FreeBox)paramObject).getData()));
    for (;;)
    {
      return false;
      if (((FreeBox)paramObject).getData() == null) {
        break;
      }
    }
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    Object localObject = this.replacers.iterator();
    for (;;)
    {
      if (!((Iterator)localObject).hasNext())
      {
        localObject = ByteBuffer.allocate(8);
        IsoTypeWriter.writeUInt32((ByteBuffer)localObject, this.data.limit() + 8);
        ((ByteBuffer)localObject).put("free".getBytes());
        ((ByteBuffer)localObject).rewind();
        paramWritableByteChannel.write((ByteBuffer)localObject);
        ((ByteBuffer)localObject).rewind();
        this.data.rewind();
        paramWritableByteChannel.write(this.data);
        this.data.rewind();
        return;
      }
      ((Box)((Iterator)localObject).next()).getBox(paramWritableByteChannel);
    }
  }
  
  public ByteBuffer getData()
  {
    if (this.data != null) {
      return (ByteBuffer)this.data.duplicate().rewind();
    }
    return null;
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
    long l = 8L;
    Iterator localIterator = this.replacers.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return l + this.data.limit();
      }
      l += ((Box)localIterator.next()).getSize();
    }
  }
  
  public String getType()
  {
    return "free";
  }
  
  public int hashCode()
  {
    if (this.data != null) {
      return this.data.hashCode();
    }
    return 0;
  }
  
  public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    this.offset = (paramDataSource.position() - paramByteBuffer.remaining());
    if (paramLong > 1048576L)
    {
      this.data = paramDataSource.map(paramDataSource.position(), paramLong);
      paramDataSource.position(paramDataSource.position() + paramLong);
      return;
    }
    assert (paramLong < 2147483647L);
    this.data = ByteBuffer.allocate(CastUtils.l2i(paramLong));
    paramDataSource.read(this.data);
  }
  
  public void setData(ByteBuffer paramByteBuffer)
  {
    this.data = paramByteBuffer;
  }
  
  public void setParent(Container paramContainer)
  {
    this.parent = paramContainer;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/FreeBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */