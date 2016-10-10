package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Logger;
import com.googlecode.mp4parser.util.Path;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public abstract class AbstractBox
  implements Box
{
  private static Logger LOG;
  private ByteBuffer content;
  long contentStartPosition;
  DataSource dataSource;
  private ByteBuffer deadBytes = null;
  boolean isParsed;
  boolean isRead;
  long memMapSize = -1L;
  long offset;
  private Container parent;
  protected String type;
  private byte[] userType;
  
  static
  {
    if (!AbstractBox.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      LOG = Logger.getLogger(AbstractBox.class);
      return;
    }
  }
  
  protected AbstractBox(String paramString)
  {
    this.type = paramString;
    this.isRead = true;
    this.isParsed = true;
  }
  
  protected AbstractBox(String paramString, byte[] paramArrayOfByte)
  {
    this.type = paramString;
    this.userType = paramArrayOfByte;
    this.isRead = true;
    this.isParsed = true;
  }
  
  private void getHeader(ByteBuffer paramByteBuffer)
  {
    if (isSmallBox())
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, getSize());
      paramByteBuffer.put(IsoFile.fourCCtoBytes(getType()));
    }
    for (;;)
    {
      if ("uuid".equals(getType())) {
        paramByteBuffer.put(getUserType());
      }
      return;
      IsoTypeWriter.writeUInt32(paramByteBuffer, 1L);
      paramByteBuffer.put(IsoFile.fourCCtoBytes(getType()));
      IsoTypeWriter.writeUInt64(paramByteBuffer, getSize());
    }
  }
  
  private boolean isSmallBox()
  {
    int i = 8;
    if ("uuid".equals(getType())) {
      i = 8 + 16;
    }
    if (this.isRead) {
      if (this.isParsed)
      {
        l = getContentSize();
        if (this.deadBytes != null)
        {
          j = this.deadBytes.limit();
          if (l + j + i >= 4294967296L) {
            break label76;
          }
        }
      }
    }
    label76:
    while (this.memMapSize + i < 4294967296L)
    {
      do
      {
        for (;;)
        {
          long l;
          return true;
          int j = 0;
        }
        return false;
      } while (this.content.limit() + i < 4294967296L);
      return false;
    }
    return false;
  }
  
  /* Error */
  private void readContent()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 60	com/googlecode/mp4parser/AbstractBox:isRead	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifne +54 -> 62
    //   11: getstatic 45	com/googlecode/mp4parser/AbstractBox:LOG	Lcom/googlecode/mp4parser/util/Logger;
    //   14: new 127	java/lang/StringBuilder
    //   17: dup
    //   18: ldc -127
    //   20: invokespecial 131	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   23: aload_0
    //   24: invokevirtual 84	com/googlecode/mp4parser/AbstractBox:getType	()Ljava/lang/String;
    //   27: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   30: invokevirtual 138	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   33: invokevirtual 141	com/googlecode/mp4parser/util/Logger:logDebug	(Ljava/lang/String;)V
    //   36: aload_0
    //   37: aload_0
    //   38: getfield 143	com/googlecode/mp4parser/AbstractBox:dataSource	Lcom/googlecode/mp4parser/DataSource;
    //   41: aload_0
    //   42: getfield 145	com/googlecode/mp4parser/AbstractBox:contentStartPosition	J
    //   45: aload_0
    //   46: getfield 54	com/googlecode/mp4parser/AbstractBox:memMapSize	J
    //   49: invokeinterface 151 5 0
    //   54: putfield 122	com/googlecode/mp4parser/AbstractBox:content	Ljava/nio/ByteBuffer;
    //   57: aload_0
    //   58: iconst_1
    //   59: putfield 60	com/googlecode/mp4parser/AbstractBox:isRead	Z
    //   62: aload_0
    //   63: monitorexit
    //   64: return
    //   65: astore_2
    //   66: new 153	java/lang/RuntimeException
    //   69: dup
    //   70: aload_2
    //   71: invokespecial 156	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   74: athrow
    //   75: astore_2
    //   76: aload_0
    //   77: monitorexit
    //   78: aload_2
    //   79: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	80	0	this	AbstractBox
    //   6	2	1	bool	boolean
    //   65	6	2	localIOException	IOException
    //   75	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   11	57	65	java/io/IOException
    //   2	7	75	finally
    //   11	57	75	finally
    //   57	62	75	finally
    //   66	75	75	finally
  }
  
  private boolean verify(ByteBuffer paramByteBuffer)
  {
    long l = getContentSize();
    ByteBuffer localByteBuffer;
    if (this.deadBytes != null)
    {
      i = this.deadBytes.limit();
      localByteBuffer = ByteBuffer.allocate(CastUtils.l2i(i + l));
      getContent(localByteBuffer);
      if (this.deadBytes != null) {
        this.deadBytes.rewind();
      }
    }
    for (;;)
    {
      if (this.deadBytes.remaining() <= 0)
      {
        paramByteBuffer.rewind();
        localByteBuffer.rewind();
        if (paramByteBuffer.remaining() == localByteBuffer.remaining()) {
          break label207;
        }
        System.err.print(getType() + ": remaining differs " + paramByteBuffer.remaining() + " vs. " + localByteBuffer.remaining());
        LOG.logError(getType() + ": remaining differs " + paramByteBuffer.remaining() + " vs. " + localByteBuffer.remaining());
        return false;
        i = 0;
        break;
      }
      localByteBuffer.put(this.deadBytes);
    }
    label207:
    int k = paramByteBuffer.position();
    int j = paramByteBuffer.limit() - 1;
    int i = localByteBuffer.limit() - 1;
    for (;;)
    {
      if (j < k) {
        return true;
      }
      byte b1 = paramByteBuffer.get(j);
      byte b2 = localByteBuffer.get(i);
      if (b1 != b2)
      {
        LOG.logError(String.format("%s: buffers differ at %d: %2X/%2X", new Object[] { getType(), Integer.valueOf(j), Byte.valueOf(b1), Byte.valueOf(b2) }));
        byte[] arrayOfByte1 = new byte[paramByteBuffer.remaining()];
        byte[] arrayOfByte2 = new byte[localByteBuffer.remaining()];
        paramByteBuffer.get(arrayOfByte1);
        localByteBuffer.get(arrayOfByte2);
        System.err.println("original      : " + Hex.encodeHex(arrayOfByte1, 4));
        System.err.println("reconstructed : " + Hex.encodeHex(arrayOfByte2, 4));
        return false;
      }
      j -= 1;
      i -= 1;
    }
  }
  
  protected abstract void _parseDetails(ByteBuffer paramByteBuffer);
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    int j = 8;
    int i = 16;
    ByteBuffer localByteBuffer;
    if (this.isRead)
    {
      if (this.isParsed)
      {
        localByteBuffer = ByteBuffer.allocate(CastUtils.l2i(getSize()));
        getHeader(localByteBuffer);
        getContent(localByteBuffer);
        if (this.deadBytes != null) {
          this.deadBytes.rewind();
        }
        for (;;)
        {
          if (this.deadBytes.remaining() <= 0)
          {
            paramWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
            return;
          }
          localByteBuffer.put(this.deadBytes);
        }
      }
      if (isSmallBox()) {
        if (!"uuid".equals(getType())) {
          break label171;
        }
      }
      for (;;)
      {
        localByteBuffer = ByteBuffer.allocate(j + i);
        getHeader(localByteBuffer);
        paramWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
        paramWritableByteChannel.write((ByteBuffer)this.content.position(0));
        return;
        j = 16;
        break;
        label171:
        i = 0;
      }
    }
    if (isSmallBox()) {
      if (!"uuid".equals(getType())) {
        break label250;
      }
    }
    for (;;)
    {
      localByteBuffer = ByteBuffer.allocate(j + i);
      getHeader(localByteBuffer);
      paramWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
      this.dataSource.transferTo(this.contentStartPosition, this.memMapSize, paramWritableByteChannel);
      return;
      j = 16;
      break;
      label250:
      i = 0;
    }
  }
  
  protected abstract void getContent(ByteBuffer paramByteBuffer);
  
  protected abstract long getContentSize();
  
  public long getOffset()
  {
    return this.offset;
  }
  
  @DoNotParseDetail
  public Container getParent()
  {
    return this.parent;
  }
  
  @DoNotParseDetail
  public String getPath()
  {
    return Path.createPath(this);
  }
  
  public long getSize()
  {
    int k = 0;
    long l1;
    label34:
    int j;
    label49:
    long l2;
    if (this.isRead) {
      if (this.isParsed)
      {
        l1 = getContentSize();
        if (l1 < 4294967288L) {
          break label112;
        }
        i = 8;
        if (!"uuid".equals(getType())) {
          break label117;
        }
        j = 16;
        l2 = j + (i + 8);
        if (this.deadBytes != null) {
          break label122;
        }
      }
    }
    label112:
    label117:
    label122:
    for (int i = k;; i = this.deadBytes.limit())
    {
      return l1 + l2 + i;
      if (this.content != null) {}
      for (i = this.content.limit();; i = 0)
      {
        l1 = i;
        break;
      }
      l1 = this.memMapSize;
      break;
      i = 0;
      break label34;
      j = 0;
      break label49;
    }
  }
  
  @DoNotParseDetail
  public String getType()
  {
    return this.type;
  }
  
  @DoNotParseDetail
  public byte[] getUserType()
  {
    return this.userType;
  }
  
  public boolean isParsed()
  {
    return this.isParsed;
  }
  
  @DoNotParseDetail
  public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    this.contentStartPosition = paramDataSource.position();
    this.offset = (this.contentStartPosition - paramByteBuffer.remaining());
    this.memMapSize = paramLong;
    this.dataSource = paramDataSource;
    paramDataSource.position(paramDataSource.position() + paramLong);
    this.isRead = false;
    this.isParsed = false;
  }
  
  public final void parseDetails()
  {
    try
    {
      readContent();
      LOG.logDebug("parsing details of " + getType());
      if (this.content != null)
      {
        ByteBuffer localByteBuffer = this.content;
        this.isParsed = true;
        localByteBuffer.rewind();
        _parseDetails(localByteBuffer);
        if (localByteBuffer.remaining() > 0) {
          this.deadBytes = localByteBuffer.slice();
        }
        this.content = null;
        if ((!$assertionsDisabled) && (!verify(localByteBuffer))) {
          throw new AssertionError();
        }
      }
    }
    finally {}
  }
  
  protected void setDeadBytes(ByteBuffer paramByteBuffer)
  {
    this.deadBytes = paramByteBuffer;
  }
  
  @DoNotParseDetail
  public void setParent(Container paramContainer)
  {
    this.parent = paramContainer;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/AbstractBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */