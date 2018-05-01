package org.telegram.messenger.exoplayer2.decoder;

import java.util.LinkedList;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract class SimpleDecoder<I extends DecoderInputBuffer, O extends OutputBuffer, E extends Exception>
  implements Decoder<I, O, E>
{
  private int availableInputBufferCount;
  private final I[] availableInputBuffers;
  private int availableOutputBufferCount;
  private final O[] availableOutputBuffers;
  private final Thread decodeThread;
  private I dequeuedInputBuffer;
  private E exception;
  private boolean flushed;
  private final Object lock = new Object();
  private final LinkedList<I> queuedInputBuffers = new LinkedList();
  private final LinkedList<O> queuedOutputBuffers = new LinkedList();
  private boolean released;
  private int skippedOutputBufferCount;
  
  protected SimpleDecoder(I[] paramArrayOfI, O[] paramArrayOfO)
  {
    this.availableInputBuffers = paramArrayOfI;
    this.availableInputBufferCount = paramArrayOfI.length;
    for (int i = 0; i < this.availableInputBufferCount; i++) {
      this.availableInputBuffers[i] = createInputBuffer();
    }
    this.availableOutputBuffers = paramArrayOfO;
    this.availableOutputBufferCount = paramArrayOfO.length;
    for (i = 0; i < this.availableOutputBufferCount; i++) {
      this.availableOutputBuffers[i] = createOutputBuffer();
    }
    this.decodeThread = new Thread()
    {
      public void run()
      {
        SimpleDecoder.this.run();
      }
    };
    this.decodeThread.start();
  }
  
  private boolean canDecodeBuffer()
  {
    if ((!this.queuedInputBuffers.isEmpty()) && (this.availableOutputBufferCount > 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean decode()
    throws InterruptedException
  {
    boolean bool1 = false;
    synchronized (this.lock)
    {
      if ((!this.released) && (!canDecodeBuffer())) {
        this.lock.wait();
      }
    }
    if (this.released) {
      return bool1;
    }
    DecoderInputBuffer localDecoderInputBuffer = (DecoderInputBuffer)this.queuedInputBuffers.removeFirst();
    ??? = this.availableOutputBuffers;
    int i = this.availableOutputBufferCount - 1;
    this.availableOutputBufferCount = i;
    ??? = ???[i];
    boolean bool2 = this.flushed;
    this.flushed = false;
    if (localDecoderInputBuffer.isEndOfStream()) {
      ((OutputBuffer)???).addFlag(4);
    }
    for (;;)
    {
      synchronized (this.lock)
      {
        if (this.flushed)
        {
          releaseOutputBufferInternal((OutputBuffer)???);
          releaseInputBufferInternal(localDecoderInputBuffer);
          bool1 = true;
          break;
          if (localDecoderInputBuffer.isDecodeOnly()) {
            ((OutputBuffer)???).addFlag(Integer.MIN_VALUE);
          }
          try
          {
            this.exception = decode(localDecoderInputBuffer, (OutputBuffer)???, bool2);
            if (this.exception == null) {
              continue;
            }
            synchronized (this.lock)
            {
              break;
            }
          }
          catch (RuntimeException localRuntimeException)
          {
            this.exception = createUnexpectedDecodeException(localRuntimeException);
            continue;
          }
          catch (OutOfMemoryError localOutOfMemoryError)
          {
            this.exception = createUnexpectedDecodeException(localOutOfMemoryError);
            continue;
          }
        }
        if (((OutputBuffer)???).isDecodeOnly())
        {
          this.skippedOutputBufferCount += 1;
          releaseOutputBufferInternal((OutputBuffer)???);
        }
      }
      ((OutputBuffer)???).skippedOutputBufferCount = this.skippedOutputBufferCount;
      this.skippedOutputBufferCount = 0;
      this.queuedOutputBuffers.addLast(???);
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
  
  private void releaseInputBufferInternal(I paramI)
  {
    paramI.clear();
    DecoderInputBuffer[] arrayOfDecoderInputBuffer = this.availableInputBuffers;
    int i = this.availableInputBufferCount;
    this.availableInputBufferCount = (i + 1);
    arrayOfDecoderInputBuffer[i] = paramI;
  }
  
  private void releaseOutputBufferInternal(O paramO)
  {
    paramO.clear();
    OutputBuffer[] arrayOfOutputBuffer = this.availableOutputBuffers;
    int i = this.availableOutputBufferCount;
    this.availableOutputBufferCount = (i + 1);
    arrayOfOutputBuffer[i] = paramO;
  }
  
  private void run()
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
  
  protected abstract I createInputBuffer();
  
  protected abstract O createOutputBuffer();
  
  protected abstract E createUnexpectedDecodeException(Throwable paramThrowable);
  
  protected abstract E decode(I paramI, O paramO, boolean paramBoolean);
  
  public final I dequeueInputBuffer()
    throws Exception
  {
    synchronized (this.lock)
    {
      maybeThrowException();
      if (this.dequeuedInputBuffer == null) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        if (this.availableInputBufferCount != 0) {
          break;
        }
        localObject2 = null;
        this.dequeuedInputBuffer = ((DecoderInputBuffer)localObject2);
        localObject2 = this.dequeuedInputBuffer;
        return (I)localObject2;
      }
      Object localObject2 = this.availableInputBuffers;
      int i = this.availableInputBufferCount - 1;
      this.availableInputBufferCount = i;
      localObject2 = localObject2[i];
    }
  }
  
  public final O dequeueOutputBuffer()
    throws Exception
  {
    synchronized (this.lock)
    {
      maybeThrowException();
      if (this.queuedOutputBuffers.isEmpty())
      {
        localObject2 = null;
        return (O)localObject2;
      }
      Object localObject2 = (OutputBuffer)this.queuedOutputBuffers.removeFirst();
    }
  }
  
  public final void flush()
  {
    synchronized (this.lock)
    {
      this.flushed = true;
      this.skippedOutputBufferCount = 0;
      if (this.dequeuedInputBuffer != null)
      {
        releaseInputBufferInternal(this.dequeuedInputBuffer);
        this.dequeuedInputBuffer = null;
      }
      if (!this.queuedInputBuffers.isEmpty()) {
        releaseInputBufferInternal((DecoderInputBuffer)this.queuedInputBuffers.removeFirst());
      }
    }
    while (!this.queuedOutputBuffers.isEmpty()) {
      releaseOutputBufferInternal((OutputBuffer)this.queuedOutputBuffers.removeFirst());
    }
  }
  
  public final void queueInputBuffer(I paramI)
    throws Exception
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
    try
    {
      this.decodeThread.join();
      return;
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;)
      {
        Thread.currentThread().interrupt();
      }
    }
  }
  
  protected void releaseOutputBuffer(O paramO)
  {
    synchronized (this.lock)
    {
      releaseOutputBufferInternal(paramO);
      maybeNotifyDecodeLoop();
      return;
    }
  }
  
  protected final void setInitialInputBufferSize(int paramInt)
  {
    int i = 0;
    if (this.availableInputBufferCount == this.availableInputBuffers.length) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      DecoderInputBuffer[] arrayOfDecoderInputBuffer = this.availableInputBuffers;
      int j = arrayOfDecoderInputBuffer.length;
      while (i < j)
      {
        arrayOfDecoderInputBuffer[i].ensureSpaceForWrite(paramInt);
        i++;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/decoder/SimpleDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */