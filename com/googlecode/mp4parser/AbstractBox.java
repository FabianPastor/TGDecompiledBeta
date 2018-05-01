package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
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
    boolean bool = true;
    int i = 8;
    if ("uuid".equals(getType())) {
      i = 8 + 16;
    }
    int j;
    if (this.isRead) {
      if (this.isParsed)
      {
        long l = getContentSize();
        if (this.deadBytes != null)
        {
          j = this.deadBytes.limit();
          if (l + j + i >= 4294967296L) {
            break label81;
          }
        }
      }
    }
    for (;;)
    {
      return bool;
      j = 0;
      break;
      label81:
      bool = false;
      continue;
      if (this.content.limit() + i >= 4294967296L)
      {
        bool = false;
        continue;
        if (this.memMapSize + i >= 4294967296L) {
          bool = false;
        }
      }
    }
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
    //   8: ifne +58 -> 66
    //   11: getstatic 45	com/googlecode/mp4parser/AbstractBox:LOG	Lcom/googlecode/mp4parser/util/Logger;
    //   14: astore_2
    //   15: new 127	java/lang/StringBuilder
    //   18: astore_3
    //   19: aload_3
    //   20: ldc -127
    //   22: invokespecial 131	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   25: aload_2
    //   26: aload_3
    //   27: aload_0
    //   28: invokevirtual 84	com/googlecode/mp4parser/AbstractBox:getType	()Ljava/lang/String;
    //   31: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   34: invokevirtual 138	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   37: invokevirtual 141	com/googlecode/mp4parser/util/Logger:logDebug	(Ljava/lang/String;)V
    //   40: aload_0
    //   41: aload_0
    //   42: getfield 143	com/googlecode/mp4parser/AbstractBox:dataSource	Lcom/googlecode/mp4parser/DataSource;
    //   45: aload_0
    //   46: getfield 145	com/googlecode/mp4parser/AbstractBox:contentStartPosition	J
    //   49: aload_0
    //   50: getfield 54	com/googlecode/mp4parser/AbstractBox:memMapSize	J
    //   53: invokeinterface 151 5 0
    //   58: putfield 122	com/googlecode/mp4parser/AbstractBox:content	Ljava/nio/ByteBuffer;
    //   61: aload_0
    //   62: iconst_1
    //   63: putfield 60	com/googlecode/mp4parser/AbstractBox:isRead	Z
    //   66: aload_0
    //   67: monitorexit
    //   68: return
    //   69: astore_3
    //   70: new 153	java/lang/RuntimeException
    //   73: astore_2
    //   74: aload_2
    //   75: aload_3
    //   76: invokespecial 156	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   79: aload_2
    //   80: athrow
    //   81: astore_3
    //   82: aload_0
    //   83: monitorexit
    //   84: aload_3
    //   85: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	86	0	this	AbstractBox
    //   6	2	1	bool	boolean
    //   14	66	2	localObject1	Object
    //   18	9	3	localStringBuilder	StringBuilder
    //   69	7	3	localIOException	IOException
    //   81	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   11	61	69	java/io/IOException
    //   2	7	81	finally
    //   11	61	81	finally
    //   61	66	81	finally
    //   70	81	81	finally
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
    boolean bool;
    for (;;)
    {
      if (this.deadBytes.remaining() <= 0)
      {
        paramByteBuffer.rewind();
        localByteBuffer.rewind();
        if (paramByteBuffer.remaining() == localByteBuffer.remaining()) {
          break label209;
        }
        System.err.print(getType() + ": remaining differs " + paramByteBuffer.remaining() + " vs. " + localByteBuffer.remaining());
        LOG.logError(getType() + ": remaining differs " + paramByteBuffer.remaining() + " vs. " + localByteBuffer.remaining());
        bool = false;
        return bool;
        i = 0;
        break;
      }
      localByteBuffer.put(this.deadBytes);
    }
    label209:
    int j = paramByteBuffer.position();
    int k = paramByteBuffer.limit() - 1;
    for (int i = localByteBuffer.limit() - 1;; i--)
    {
      if (k < j)
      {
        bool = true;
        break;
      }
      byte b1 = paramByteBuffer.get(k);
      byte b2 = localByteBuffer.get(i);
      if (b1 != b2)
      {
        LOG.logError(String.format("%s: buffers differ at %d: %2X/%2X", new Object[] { getType(), Integer.valueOf(k), Byte.valueOf(b1), Byte.valueOf(b2) }));
        byte[] arrayOfByte1 = new byte[paramByteBuffer.remaining()];
        byte[] arrayOfByte2 = new byte[localByteBuffer.remaining()];
        paramByteBuffer.get(arrayOfByte1);
        localByteBuffer.get(arrayOfByte2);
        System.err.println("original      : " + Hex.encodeHex(arrayOfByte1, 4));
        System.err.println("reconstructed : " + Hex.encodeHex(arrayOfByte2, 4));
        bool = false;
        break;
      }
      k--;
    }
  }
  
  protected abstract void _parseDetails(ByteBuffer paramByteBuffer);
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    int i = 8;
    int j = 16;
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
        label105:
        if (!"uuid".equals(getType())) {
          break label173;
        }
      }
      for (;;)
      {
        localByteBuffer = ByteBuffer.allocate(i + j);
        getHeader(localByteBuffer);
        paramWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
        paramWritableByteChannel.write((ByteBuffer)this.content.position(0));
        break;
        i = 16;
        break label105;
        label173:
        j = 0;
      }
    }
    if (isSmallBox()) {
      label185:
      if (!"uuid".equals(getType())) {
        break label254;
      }
    }
    for (;;)
    {
      localByteBuffer = ByteBuffer.allocate(i + j);
      getHeader(localByteBuffer);
      paramWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
      this.dataSource.transferTo(this.contentStartPosition, this.memMapSize, paramWritableByteChannel);
      break;
      i = 16;
      break label185;
      label254:
      j = 0;
    }
  }
  
  protected abstract void getContent(ByteBuffer paramByteBuffer);
  
  protected abstract long getContentSize();
  
  public long getOffset()
  {
    return this.offset;
  }
  
  public Container getParent()
  {
    return this.parent;
  }
  
  public String getPath()
  {
    return Path.createPath(this);
  }
  
  public long getSize()
  {
    int i = 0;
    long l1;
    label33:
    int k;
    label49:
    long l2;
    if (this.isRead) {
      if (this.isParsed)
      {
        l1 = getContentSize();
        if (l1 < 4294967288L) {
          break label116;
        }
        j = 8;
        if (!"uuid".equals(getType())) {
          break label122;
        }
        k = 16;
        l2 = k + (j + 8);
        if (this.deadBytes != null) {
          break label128;
        }
      }
    }
    label116:
    label122:
    label128:
    for (int j = i;; j = this.deadBytes.limit())
    {
      return l1 + l2 + j;
      if (this.content != null) {}
      for (j = this.content.limit();; j = 0)
      {
        l1 = j;
        break;
      }
      l1 = this.memMapSize;
      break;
      j = 0;
      break label33;
      k = 0;
      break label49;
    }
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public byte[] getUserType()
  {
    return this.userType;
  }
  
  public boolean isParsed()
  {
    return this.isParsed;
  }
  
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
      Object localObject1 = LOG;
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>("parsing details of ");
      ((Logger)localObject1).logDebug(getType());
      if (this.content != null)
      {
        localObject1 = this.content;
        this.isParsed = true;
        ((ByteBuffer)localObject1).rewind();
        _parseDetails((ByteBuffer)localObject1);
        if (((ByteBuffer)localObject1).remaining() > 0) {
          this.deadBytes = ((ByteBuffer)localObject1).slice();
        }
        this.content = null;
        if ((!$assertionsDisabled) && (!verify((ByteBuffer)localObject1)))
        {
          localObject1 = new java/lang/AssertionError;
          ((AssertionError)localObject1).<init>();
          throw ((Throwable)localObject1);
        }
      }
    }
    finally {}
  }
  
  protected void setDeadBytes(ByteBuffer paramByteBuffer)
  {
    this.deadBytes = paramByteBuffer;
  }
  
  public void setParent(Container paramContainer)
  {
    this.parent = paramContainer;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/AbstractBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */