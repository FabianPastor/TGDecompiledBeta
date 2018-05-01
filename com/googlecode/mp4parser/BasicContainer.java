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
  implements Container, Closeable, Iterator<Box>
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
    if ((this.dataSource != null) && (this.lookahead != EOF)) {}
    for (Object localObject = new LazyList(this.boxes, this);; localObject = this.boxes) {
      return (List<Box>)localObject;
    }
  }
  
  public <T extends Box> List<T> getBoxes(Class<T> paramClass)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    List localList = getBoxes();
    int i = 0;
    if (i >= localList.size()) {
      if (localObject1 == null) {
        break label129;
      }
    }
    for (;;)
    {
      return (List<T>)localObject1;
      Box localBox = (Box)localList.get(i);
      Object localObject3 = localObject1;
      Object localObject4 = localObject2;
      if (paramClass.isInstance(localBox))
      {
        if (localObject2 != null) {
          break label83;
        }
        localObject4 = localBox;
        localObject3 = localObject1;
      }
      for (;;)
      {
        i++;
        localObject1 = localObject3;
        localObject2 = localObject4;
        break;
        label83:
        localObject4 = localObject1;
        if (localObject1 == null)
        {
          localObject4 = new ArrayList(2);
          ((List)localObject4).add(localObject2);
        }
        ((List)localObject4).add(localBox);
        localObject3 = localObject4;
        localObject4 = localObject2;
      }
      label129:
      if (localObject2 != null) {
        localObject1 = Collections.singletonList(localObject2);
      } else {
        localObject1 = Collections.emptyList();
      }
    }
  }
  
  public <T extends Box> List<T> getBoxes(Class<T> paramClass, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList(2);
    List localList = getBoxes();
    for (int i = 0;; i++)
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
    }
  }
  
  public ByteBuffer getByteBuffer(long paramLong1, long paramLong2)
    throws IOException
  {
    if (this.dataSource != null) {
      synchronized (this.dataSource)
      {
        ByteBuffer localByteBuffer1 = this.dataSource.map(this.startPosition + paramLong1, paramLong2);
        return localByteBuffer1;
      }
    }
    ByteBuffer localByteBuffer2 = ByteBuffer.allocate(CastUtils.l2i(paramLong2));
    long l1 = paramLong1 + paramLong2;
    paramLong2 = 0L;
    Object localObject3 = this.boxes.iterator();
    for (;;)
    {
      long l2 = paramLong2;
      if (!((Iterator)localObject3).hasNext())
      {
        localObject3 = (ByteBuffer)localByteBuffer2.rewind();
        break;
      }
      ??? = (Box)((Iterator)localObject3).next();
      long l3 = l2 + ((Box)???).getSize();
      paramLong2 = l3;
      if (l3 > paramLong1)
      {
        paramLong2 = l3;
        if (l2 < l1)
        {
          ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
          WritableByteChannel localWritableByteChannel = Channels.newChannel(localByteArrayOutputStream);
          ((Box)???).getBox(localWritableByteChannel);
          localWritableByteChannel.close();
          if ((l2 >= paramLong1) && (l3 <= l1))
          {
            localByteBuffer2.put(localByteArrayOutputStream.toByteArray());
            paramLong2 = l3;
          }
          else
          {
            int i;
            if ((l2 < paramLong1) && (l3 > l1))
            {
              i = CastUtils.l2i(((Box)???).getSize() - (paramLong1 - l2) - (l3 - l1));
              localByteBuffer2.put(localByteArrayOutputStream.toByteArray(), CastUtils.l2i(paramLong1 - l2), i);
              paramLong2 = l3;
            }
            else if ((l2 < paramLong1) && (l3 <= l1))
            {
              i = CastUtils.l2i(((Box)???).getSize() - (paramLong1 - l2));
              localByteBuffer2.put(localByteArrayOutputStream.toByteArray(), CastUtils.l2i(paramLong1 - l2), i);
              paramLong2 = l3;
            }
            else
            {
              paramLong2 = l3;
              if (l2 >= paramLong1)
              {
                paramLong2 = l3;
                if (l3 > l1)
                {
                  i = CastUtils.l2i(((Box)???).getSize() - (l3 - l1));
                  localByteBuffer2.put(localByteArrayOutputStream.toByteArray(), 0, i);
                  paramLong2 = l3;
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
    for (int i = 0;; i++)
    {
      if (i >= getBoxes().size()) {
        return l;
      }
      l += ((Box)this.boxes.get(i)).getSize();
    }
  }
  
  public boolean hasNext()
  {
    boolean bool = false;
    if (this.lookahead == EOF) {}
    for (;;)
    {
      return bool;
      if (this.lookahead != null) {
        bool = true;
      } else {
        try
        {
          this.lookahead = next();
          bool = true;
        }
        catch (NoSuchElementException localNoSuchElementException)
        {
          this.lookahead = EOF;
        }
      }
    }
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
    //   67: astore_2
    //   68: aload_2
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
    //   97: astore_1
    //   98: aload_0
    //   99: aload_0
    //   100: getfield 89	com/googlecode/mp4parser/BasicContainer:dataSource	Lcom/googlecode/mp4parser/DataSource;
    //   103: invokeinterface 211 1 0
    //   108: putfield 54	com/googlecode/mp4parser/BasicContainer:parsePosition	J
    //   111: aload_2
    //   112: monitorexit
    //   113: goto -86 -> 27
    //   116: astore_1
    //   117: aload_2
    //   118: monitorexit
    //   119: aload_1
    //   120: athrow
    //   121: astore_1
    //   122: new 203	java/util/NoSuchElementException
    //   125: dup
    //   126: invokespecial 219	java/util/NoSuchElementException:<init>	()V
    //   129: athrow
    //   130: astore_1
    //   131: new 203	java/util/NoSuchElementException
    //   134: dup
    //   135: invokespecial 219	java/util/NoSuchElementException:<init>	()V
    //   138: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	139	0	this	BasicContainer
    //   21	77	1	localBox	Box
    //   116	4	1	localObject	Object
    //   121	1	1	localEOFException	java.io.EOFException
    //   130	1	1	localIOException	IOException
    // Exception table:
    //   from	to	target	type
    //   70	113	116	finally
    //   117	119	116	finally
    //   63	70	121	java/io/EOFException
    //   119	121	121	java/io/EOFException
    //   63	70	130	java/io/IOException
    //   119	121	130	java/io/IOException
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
    for (int i = 0;; i++)
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