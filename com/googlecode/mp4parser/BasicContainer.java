package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.LazyList;
import com.googlecode.mp4parser.util.Logger;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class BasicContainer
  implements Container, Iterator<Box>, Closeable
{
  private static final Box EOF = new AbstractBox("eof ")
  {
    protected void _parseDetails(ByteBuffer paramAnonymousByteBuffer) {}
    
    protected void getContent(ByteBuffer paramAnonymousByteBuffer) {}
    
    protected long getContentSize()
    {
      return 0L;
    }
  };
  private static Logger LOG = Logger.getLogger(BasicContainer.class);
  protected BoxParser boxParser;
  private List<Box> boxes = new ArrayList();
  protected DataSource dataSource;
  long endPosition = 0L;
  Box lookahead = null;
  long parsePosition = 0L;
  long startPosition = 0L;
  
  public void addBox(Box paramBox)
  {
    if (paramBox != null)
    {
      this.boxes = new ArrayList(getBoxes());
      paramBox.setParent(this);
      this.boxes.add(paramBox);
    }
  }
  
  public void close()
    throws IOException
  {
    this.dataSource.close();
  }
  
  public List<Box> getBoxes()
  {
    if ((this.dataSource != null) && (this.lookahead != EOF)) {
      return new LazyList(this.boxes, this);
    }
    return this.boxes;
  }
  
  public <T extends Box> List<T> getBoxes(Class<T> paramClass)
  {
    Object localObject1 = null;
    Object localObject3 = null;
    List localList = getBoxes();
    int i = 0;
    if (i >= localList.size())
    {
      if (localObject1 != null) {
        return (List<T>)localObject1;
      }
    }
    else
    {
      Box localBox = (Box)localList.get(i);
      Object localObject2 = localObject1;
      Object localObject4 = localObject3;
      if (paramClass.isInstance(localBox))
      {
        if (localObject3 != null) {
          break label85;
        }
        localObject4 = localBox;
        localObject2 = localObject1;
      }
      for (;;)
      {
        i += 1;
        localObject1 = localObject2;
        localObject3 = localObject4;
        break;
        label85:
        localObject2 = localObject1;
        if (localObject1 == null)
        {
          localObject2 = new ArrayList(2);
          ((List)localObject2).add(localObject3);
        }
        ((List)localObject2).add(localBox);
        localObject4 = localObject3;
      }
    }
    if (localObject3 != null) {
      return Collections.singletonList(localObject3);
    }
    return Collections.emptyList();
  }
  
  public <T extends Box> List<T> getBoxes(Class<T> paramClass, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList(2);
    List localList = getBoxes();
    int i = 0;
    for (;;)
    {
      if (i >= localList.size()) {
        return localArrayList;
      }
      Box localBox = (Box)localList.get(i);
      if (paramClass.isInstance(localBox)) {
        localArrayList.add(localBox);
      }
      if ((paramBoolean) && ((localBox instanceof Container))) {
        localArrayList.addAll(((Container)localBox).getBoxes(paramClass, paramBoolean));
      }
      i += 1;
    }
  }
  
  public ByteBuffer getByteBuffer(long paramLong1, long paramLong2)
    throws IOException
  {
    if (this.dataSource != null) {
      synchronized (this.dataSource)
      {
        ByteBuffer localByteBuffer = this.dataSource.map(this.startPosition + paramLong1, paramLong2);
        return localByteBuffer;
      }
    }
    ??? = ByteBuffer.allocate(CastUtils.l2i(paramLong2));
    long l3 = paramLong1 + paramLong2;
    paramLong2 = 0L;
    Iterator localIterator = this.boxes.iterator();
    for (;;)
    {
      long l1 = paramLong2;
      if (!localIterator.hasNext()) {
        return (ByteBuffer)((ByteBuffer)???).rewind();
      }
      Box localBox = (Box)localIterator.next();
      long l2 = l1 + localBox.getSize();
      paramLong2 = l2;
      if (l2 > paramLong1)
      {
        paramLong2 = l2;
        if (l1 < l3)
        {
          ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
          WritableByteChannel localWritableByteChannel = Channels.newChannel(localByteArrayOutputStream);
          localBox.getBox(localWritableByteChannel);
          localWritableByteChannel.close();
          if ((l1 >= paramLong1) && (l2 <= l3))
          {
            ((ByteBuffer)???).put(localByteArrayOutputStream.toByteArray());
            paramLong2 = l2;
          }
          else
          {
            int i;
            if ((l1 < paramLong1) && (l2 > l3))
            {
              i = CastUtils.l2i(localBox.getSize() - (paramLong1 - l1) - (l2 - l3));
              ((ByteBuffer)???).put(localByteArrayOutputStream.toByteArray(), CastUtils.l2i(paramLong1 - l1), i);
              paramLong2 = l2;
            }
            else if ((l1 < paramLong1) && (l2 <= l3))
            {
              i = CastUtils.l2i(localBox.getSize() - (paramLong1 - l1));
              ((ByteBuffer)???).put(localByteArrayOutputStream.toByteArray(), CastUtils.l2i(paramLong1 - l1), i);
              paramLong2 = l2;
            }
            else
            {
              paramLong2 = l2;
              if (l1 >= paramLong1)
              {
                paramLong2 = l2;
                if (l2 > l3)
                {
                  i = CastUtils.l2i(localBox.getSize() - (l2 - l3));
                  ((ByteBuffer)???).put(localByteArrayOutputStream.toByteArray(), 0, i);
                  paramLong2 = l2;
                }
              }
            }
          }
        }
      }
    }
  }
  
  protected long getContainerSize()
  {
    long l = 0L;
    int i = 0;
    for (;;)
    {
      if (i >= getBoxes().size()) {
        return l;
      }
      l += ((Box)this.boxes.get(i)).getSize();
      i += 1;
    }
  }
  
  public boolean hasNext()
  {
    if (this.lookahead == EOF) {
      return false;
    }
    if (this.lookahead != null) {
      return true;
    }
    try
    {
      this.lookahead = next();
      return true;
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      this.lookahead = EOF;
    }
    return false;
  }
  
  public void initContainer(DataSource paramDataSource, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    this.dataSource = paramDataSource;
    long l = paramDataSource.position();
    this.startPosition = l;
    this.parsePosition = l;
    paramDataSource.position(paramDataSource.position() + paramLong);
    this.endPosition = paramDataSource.position();
    this.boxParser = paramBoxParser;
  }
  
  /* Error */
  public Box next()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 52	com/googlecode/mp4parser/BasicContainer:lookahead	Lcom/coremedia/iso/boxes/Box;
    //   4: ifnull +25 -> 29
    //   7: aload_0
    //   8: getfield 52	com/googlecode/mp4parser/BasicContainer:lookahead	Lcom/coremedia/iso/boxes/Box;
    //   11: getstatic 39	com/googlecode/mp4parser/BasicContainer:EOF	Lcom/coremedia/iso/boxes/Box;
    //   14: if_acmpeq +15 -> 29
    //   17: aload_0
    //   18: getfield 52	com/googlecode/mp4parser/BasicContainer:lookahead	Lcom/coremedia/iso/boxes/Box;
    //   21: astore_1
    //   22: aload_0
    //   23: aconst_null
    //   24: putfield 52	com/googlecode/mp4parser/BasicContainer:lookahead	Lcom/coremedia/iso/boxes/Box;
    //   27: aload_1
    //   28: areturn
    //   29: aload_0
    //   30: getfield 89	com/googlecode/mp4parser/BasicContainer:dataSource	Lcom/googlecode/mp4parser/DataSource;
    //   33: ifnull +15 -> 48
    //   36: aload_0
    //   37: getfield 54	com/googlecode/mp4parser/BasicContainer:parsePosition	J
    //   40: aload_0
    //   41: getfield 58	com/googlecode/mp4parser/BasicContainer:endPosition	J
    //   44: lcmp
    //   45: iflt +18 -> 63
    //   48: aload_0
    //   49: getstatic 39	com/googlecode/mp4parser/BasicContainer:EOF	Lcom/coremedia/iso/boxes/Box;
    //   52: putfield 52	com/googlecode/mp4parser/BasicContainer:lookahead	Lcom/coremedia/iso/boxes/Box;
    //   55: new 203	java/util/NoSuchElementException
    //   58: dup
    //   59: invokespecial 219	java/util/NoSuchElementException:<init>	()V
    //   62: athrow
    //   63: aload_0
    //   64: getfield 89	com/googlecode/mp4parser/BasicContainer:dataSource	Lcom/googlecode/mp4parser/DataSource;
    //   67: astore_1
    //   68: aload_1
    //   69: monitorenter
    //   70: aload_0
    //   71: getfield 89	com/googlecode/mp4parser/BasicContainer:dataSource	Lcom/googlecode/mp4parser/DataSource;
    //   74: aload_0
    //   75: getfield 54	com/googlecode/mp4parser/BasicContainer:parsePosition	J
    //   78: invokeinterface 214 3 0
    //   83: aload_0
    //   84: getfield 216	com/googlecode/mp4parser/BasicContainer:boxParser	Lcom/coremedia/iso/BoxParser;
    //   87: aload_0
    //   88: getfield 89	com/googlecode/mp4parser/BasicContainer:dataSource	Lcom/googlecode/mp4parser/DataSource;
    //   91: aload_0
    //   92: invokeinterface 225 3 0
    //   97: astore_2
    //   98: aload_0
    //   99: aload_0
    //   100: getfield 89	com/googlecode/mp4parser/BasicContainer:dataSource	Lcom/googlecode/mp4parser/DataSource;
    //   103: invokeinterface 211 1 0
    //   108: putfield 54	com/googlecode/mp4parser/BasicContainer:parsePosition	J
    //   111: aload_1
    //   112: monitorexit
    //   113: aload_2
    //   114: areturn
    //   115: astore_2
    //   116: aload_1
    //   117: monitorexit
    //   118: aload_2
    //   119: athrow
    //   120: astore_1
    //   121: new 203	java/util/NoSuchElementException
    //   124: dup
    //   125: invokespecial 219	java/util/NoSuchElementException:<init>	()V
    //   128: athrow
    //   129: astore_1
    //   130: new 203	java/util/NoSuchElementException
    //   133: dup
    //   134: invokespecial 219	java/util/NoSuchElementException:<init>	()V
    //   137: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	138	0	this	BasicContainer
    //   120	1	1	localEOFException	java.io.EOFException
    //   129	1	1	localIOException	IOException
    //   97	17	2	localBox	Box
    //   115	4	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   70	113	115	finally
    //   116	118	115	finally
    //   63	70	120	java/io/EOFException
    //   118	120	120	java/io/EOFException
    //   63	70	129	java/io/IOException
    //   118	120	129	java/io/IOException
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
  
  public void setBoxes(List<Box> paramList)
  {
    this.boxes = new ArrayList(paramList);
    this.lookahead = EOF;
    this.dataSource = null;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getClass().getSimpleName()).append("[");
    int i = 0;
    for (;;)
    {
      if (i >= this.boxes.size())
      {
        localStringBuilder.append("]");
        return localStringBuilder.toString();
      }
      if (i > 0) {
        localStringBuilder.append(";");
      }
      localStringBuilder.append(((Box)this.boxes.get(i)).toString());
      i += 1;
    }
  }
  
  public final void writeContainer(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    Iterator localIterator = getBoxes().iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      ((Box)localIterator.next()).getBox(paramWritableByteChannel);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/BasicContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */