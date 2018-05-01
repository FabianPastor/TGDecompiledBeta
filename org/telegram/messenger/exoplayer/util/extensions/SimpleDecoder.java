package org.telegram.messenger.exoplayer.util.extensions;

import java.util.LinkedList;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.util.Assertions;

public abstract class SimpleDecoder<I extends InputBuffer, O extends OutputBuffer, E extends Exception>
  extends Thread
  implements Decoder<I, O, E>
{
  private int availableInputBufferCount;
  private final I[] availableInputBuffers;
  private int availableOutputBufferCount;
  private final O[] availableOutputBuffers;
  private I dequeuedInputBuffer;
  private E exception;
  private boolean flushed;
  private final Object lock = new Object();
  private final LinkedList<I> queuedInputBuffers = new LinkedList();
  private final LinkedList<O> queuedOutputBuffers = new LinkedList();
  private boolean released;
  
  protected SimpleDecoder(I[] paramArrayOfI, O[] paramArrayOfO)
  {
    this.availableInputBuffers = paramArrayOfI;
    this.availableInputBufferCount = paramArrayOfI.length;
    int i = 0;
    while (i < this.availableInputBufferCount)
    {
      this.availableInputBuffers[i] = createInputBuffer();
      i += 1;
    }
    this.availableOutputBuffers = paramArrayOfO;
    this.availableOutputBufferCount = paramArrayOfO.length;
    i = 0;
    while (i < this.availableOutputBufferCount)
    {
      this.availableOutputBuffers[i] = createOutputBuffer();
      i += 1;
    }
  }
  
  private boolean canDecodeBuffer()
  {
    return (!this.queuedInputBuffers.isEmpty()) && (this.availableOutputBufferCount > 0);
  }
  
  private boolean decode()
    throws InterruptedException
  {
    synchronized (this.lock)
    {
      if ((!this.released) && (!canDecodeBuffer())) {
        this.lock.wait();
      }
    }
    if (this.released) {
      return false;
    }
    ??? = (InputBuffer)this.queuedInputBuffers.removeFirst();
    Object localObject6 = this.availableOutputBuffers;
    int i = this.availableOutputBufferCount - 1;
    this.availableOutputBufferCount = i;
    localObject6 = localObject6[i];
    boolean bool = this.flushed;
    this.flushed = false;
    ((OutputBuffer)localObject6).reset();
    if (((InputBuffer)???).getFlag(1)) {
      ((OutputBuffer)localObject6).setFlag(1);
    }
    synchronized (this.lock)
    {
      while ((this.flushed) || (((OutputBuffer)localObject6).getFlag(2)))
      {
        OutputBuffer[] arrayOfOutputBuffer = this.availableOutputBuffers;
        i = this.availableOutputBufferCount;
        this.availableOutputBufferCount = (i + 1);
        arrayOfOutputBuffer[i] = localObject6;
        localObject6 = this.availableInputBuffers;
        i = this.availableInputBufferCount;
        this.availableInputBufferCount = (i + 1);
        localObject6[i] = ???;
        return true;
        if (((InputBuffer)???).getFlag(2)) {
          ((OutputBuffer)localObject6).setFlag(2);
        }
        this.exception = decode((InputBuffer)???, (OutputBuffer)localObject6, bool);
        if (this.exception != null) {
          synchronized (this.lock)
          {
            return false;
          }
        }
      }
      this.queuedOutputBuffers.addLast(localObject6);
    }
  }
  
  private void maybeNotifyDecodeLoop()
  {
    if (canDecodeBuffer()) {
      this.lock.notify();
    }
  }
  
  private void maybeThrowException()
    throws Exception
  {
    if (this.exception != null) {
      throw this.exception;
    }
  }
  
  protected abstract I createInputBuffer();
  
  protected abstract O createOutputBuffer();
  
  protected abstract E decode(I paramI, O paramO, boolean paramBoolean);
  
  public final I dequeueInputBuffer()
    throws Exception
  {
    for (;;)
    {
      synchronized (this.lock)
      {
        maybeThrowException();
        if (this.dequeuedInputBuffer == null)
        {
          bool = true;
          Assertions.checkState(bool);
          if (this.availableInputBufferCount == 0) {
            return null;
          }
          Object localObject2 = this.availableInputBuffers;
          int i = this.availableInputBufferCount - 1;
          this.availableInputBufferCount = i;
          localObject2 = localObject2[i];
          ((InputBuffer)localObject2).reset();
          this.dequeuedInputBuffer = ((InputBuffer)localObject2);
          return (I)localObject2;
        }
      }
      boolean bool = false;
    }
  }
  
  public final O dequeueOutputBuffer()
    throws Exception
  {
    synchronized (this.lock)
    {
      maybeThrowException();
      if (this.queuedOutputBuffers.isEmpty()) {
        return null;
      }
      OutputBuffer localOutputBuffer = (OutputBuffer)this.queuedOutputBuffers.removeFirst();
      return localOutputBuffer;
    }
  }
  
  public final void flush()
  {
    int i;
    synchronized (this.lock)
    {
      this.flushed = true;
      InputBuffer[] arrayOfInputBuffer;
      if (this.dequeuedInputBuffer != null)
      {
        arrayOfInputBuffer = this.availableInputBuffers;
        i = this.availableInputBufferCount;
        this.availableInputBufferCount = (i + 1);
        arrayOfInputBuffer[i] = this.dequeuedInputBuffer;
        this.dequeuedInputBuffer = null;
      }
      if (!this.queuedInputBuffers.isEmpty())
      {
        arrayOfInputBuffer = this.availableInputBuffers;
        i = this.availableInputBufferCount;
        this.availableInputBufferCount = (i + 1);
        arrayOfInputBuffer[i] = ((InputBuffer)this.queuedInputBuffers.removeFirst());
      }
    }
    while (!this.queuedOutputBuffers.isEmpty())
    {
      OutputBuffer[] arrayOfOutputBuffer = this.availableOutputBuffers;
      i = this.availableOutputBufferCount;
      this.availableOutputBufferCount = (i + 1);
      arrayOfOutputBuffer[i] = ((OutputBuffer)this.queuedOutputBuffers.removeFirst());
    }
  }
  
  public final void queueInputBuffer(I paramI)
    throws Exception
  {
    for (;;)
    {
      synchronized (this.lock)
      {
        maybeThrowException();
        if (paramI == this.dequeuedInputBuffer)
        {
          bool = true;
          Assertions.checkArgument(bool);
          this.queuedInputBuffers.addLast(paramI);
          maybeNotifyDecodeLoop();
          this.dequeuedInputBuffer = null;
          return;
        }
      }
      boolean bool = false;
    }
  }
  
  public void release()
  {
    synchronized (this.lock)
    {
      this.released = true;
      this.lock.notify();
    }
  }
  
  protected void releaseOutputBuffer(O paramO)
  {
    synchronized (this.lock)
    {
      OutputBuffer[] arrayOfOutputBuffer = this.availableOutputBuffers;
      int i = this.availableOutputBufferCount;
      this.availableOutputBufferCount = (i + 1);
      arrayOfOutputBuffer[i] = paramO;
      maybeNotifyDecodeLoop();
      return;
    }
  }
  
  public final void run()
  {
    try
    {
      boolean bool;
      do
      {
        bool = decode();
      } while (bool);
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new IllegalStateException(localInterruptedException);
    }
  }
  
  protected final void setInitialInputBufferSize(int paramInt)
  {
    if (this.availableInputBufferCount == this.availableInputBuffers.length) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      int i = 0;
      while (i < this.availableInputBuffers.length)
      {
        this.availableInputBuffers[i].sampleHolder.ensureSpaceForWrite(paramInt);
        i += 1;
      }
    }
  }
  
  public static abstract interface EventListener<E>
  {
    public abstract void onDecoderError(E paramE);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/extensions/SimpleDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */