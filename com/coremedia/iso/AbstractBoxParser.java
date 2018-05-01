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
        paramDataSource = null;
        return paramDataSource;
      }
    } while (i >= 0);
    paramDataSource.position(l1);
    throw new EOFException();
    String str1 = IsoTypeReader.read4cc((ByteBuffer)this.header.get());
    Object localObject = null;
    label235:
    long l2;
    if (l1 == 1L)
    {
      ((ByteBuffer)this.header.get()).limit(16);
      paramDataSource.read((ByteBuffer)this.header.get());
      ((ByteBuffer)this.header.get()).position(8);
      l1 = IsoTypeReader.readUInt64((ByteBuffer)this.header.get()) - 16L;
      l2 = l1;
      if ("uuid".equals(str1))
      {
        ((ByteBuffer)this.header.get()).limit(((ByteBuffer)this.header.get()).limit() + 16);
        paramDataSource.read((ByteBuffer)this.header.get());
        localObject = new byte[16];
        i = ((ByteBuffer)this.header.get()).position() - 16;
        label319:
        if (i < ((ByteBuffer)this.header.get()).position()) {
          break label455;
        }
        l2 = l1 - 16L;
      }
      if (!(paramContainer instanceof Box)) {
        break label498;
      }
    }
    label455:
    label498:
    for (String str2 = ((Box)paramContainer).getType();; str2 = "")
    {
      localObject = createBox(str1, (byte[])localObject, str2);
      ((Box)localObject).setParent(paramContainer);
      ((ByteBuffer)this.header.get()).rewind();
      ((Box)localObject).parse(paramDataSource, (ByteBuffer)this.header.get(), l2, this);
      paramDataSource = (DataSource)localObject;
      break;
      if (l1 == 0L)
      {
        l1 = paramDataSource.size() - paramDataSource.position();
        break label235;
      }
      l1 -= 8L;
      break label235;
      localObject[(i - (((ByteBuffer)this.header.get()).position() - 16))] = ((ByteBuffer)this.header.get()).get(i);
      i++;
      break label319;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/AbstractBoxParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */