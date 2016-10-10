package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.DataSource;
import java.io.EOFException;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public abstract class AbstractBoxParser
  implements BoxParser
{
  private static Logger LOG = Logger.getLogger(AbstractBoxParser.class.getName());
  ThreadLocal<ByteBuffer> header = new ThreadLocal()
  {
    protected ByteBuffer initialValue()
    {
      return ByteBuffer.allocate(32);
    }
  };
  
  public abstract Box createBox(String paramString1, byte[] paramArrayOfByte, String paramString2);
  
  public Box parseBox(DataSource paramDataSource, Container paramContainer)
    throws IOException
  {
    long l1 = paramDataSource.position();
    ((ByteBuffer)this.header.get()).rewind().limit(8);
    int i;
    do
    {
      i = paramDataSource.read((ByteBuffer)this.header.get());
      if (i == 8)
      {
        ((ByteBuffer)this.header.get()).rewind();
        l1 = IsoTypeReader.readUInt32((ByteBuffer)this.header.get());
        if ((l1 >= 8L) || (l1 <= 1L)) {
          break;
        }
        LOG.severe("Plausibility check failed: size < 8 (size = " + l1 + "). Stop parsing!");
        return null;
      }
    } while (i >= 0);
    paramDataSource.position(l1);
    throw new EOFException();
    String str2 = IsoTypeReader.read4cc((ByteBuffer)this.header.get());
    Object localObject = null;
    long l2;
    if (l1 == 1L)
    {
      ((ByteBuffer)this.header.get()).limit(16);
      paramDataSource.read((ByteBuffer)this.header.get());
      ((ByteBuffer)this.header.get()).position(8);
      l1 = IsoTypeReader.readUInt64((ByteBuffer)this.header.get()) - 16L;
      l2 = l1;
      if ("uuid".equals(str2))
      {
        ((ByteBuffer)this.header.get()).limit(((ByteBuffer)this.header.get()).limit() + 16);
        paramDataSource.read((ByteBuffer)this.header.get());
        localObject = new byte[16];
        i = ((ByteBuffer)this.header.get()).position() - 16;
        label322:
        if (i < ((ByteBuffer)this.header.get()).position()) {
          break label459;
        }
        l2 = l1 - 16L;
      }
      if (!(paramContainer instanceof Box)) {
        break label501;
      }
    }
    label459:
    label501:
    for (String str1 = ((Box)paramContainer).getType();; str1 = "")
    {
      localObject = createBox(str2, (byte[])localObject, str1);
      ((Box)localObject).setParent(paramContainer);
      ((ByteBuffer)this.header.get()).rewind();
      ((Box)localObject).parse(paramDataSource, (ByteBuffer)this.header.get(), l2, this);
      return (Box)localObject;
      if (l1 == 0L)
      {
        l1 = paramDataSource.size() - paramDataSource.position();
        break;
      }
      l1 -= 8L;
      break;
      localObject[(i - (((ByteBuffer)this.header.get()).position() - 16))] = ((ByteBuffer)this.header.get()).get(i);
      i += 1;
      break label322;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/AbstractBoxParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */