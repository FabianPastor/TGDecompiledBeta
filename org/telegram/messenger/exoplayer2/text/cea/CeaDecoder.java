package org.telegram.messenger.exoplayer2.text.cea;

import java.util.LinkedList;
import java.util.PriorityQueue;
import org.telegram.messenger.exoplayer2.text.Subtitle;
import org.telegram.messenger.exoplayer2.text.SubtitleDecoder;
import org.telegram.messenger.exoplayer2.text.SubtitleDecoderException;
import org.telegram.messenger.exoplayer2.text.SubtitleInputBuffer;
import org.telegram.messenger.exoplayer2.text.SubtitleOutputBuffer;
import org.telegram.messenger.exoplayer2.util.Assertions;

abstract class CeaDecoder
  implements SubtitleDecoder
{
  private static final int NUM_INPUT_BUFFERS = 10;
  private static final int NUM_OUTPUT_BUFFERS = 2;
  private final LinkedList<SubtitleInputBuffer> availableInputBuffers = new LinkedList();
  private final LinkedList<SubtitleOutputBuffer> availableOutputBuffers;
  private SubtitleInputBuffer dequeuedInputBuffer;
  private long playbackPositionUs;
  private final PriorityQueue<SubtitleInputBuffer> queuedInputBuffers;
  
  public CeaDecoder()
  {
    for (int i = 0; i < 10; i++) {
      this.availableInputBuffers.add(new SubtitleInputBuffer());
    }
    this.availableOutputBuffers = new LinkedList();
    for (i = 0; i < 2; i++) {
      this.availableOutputBuffers.add(new CeaOutputBuffer(this));
    }
    this.queuedInputBuffers = new PriorityQueue();
  }
  
  private void releaseInputBuffer(SubtitleInputBuffer paramSubtitleInputBuffer)
  {
    paramSubtitleInputBuffer.clear();
    this.availableInputBuffers.add(paramSubtitleInputBuffer);
  }
  
  protected abstract Subtitle createSubtitle();
  
  protected abstract void decode(SubtitleInputBuffer paramSubtitleInputBuffer);
  
  public SubtitleInputBuffer dequeueInputBuffer()
    throws SubtitleDecoderException
  {
    boolean bool;
    if (this.dequeuedInputBuffer == null)
    {
      bool = true;
      Assertions.checkState(bool);
      if (!this.availableInputBuffers.isEmpty()) {
        break label32;
      }
    }
    for (SubtitleInputBuffer localSubtitleInputBuffer = null;; localSubtitleInputBuffer = this.dequeuedInputBuffer)
    {
      return localSubtitleInputBuffer;
      bool = false;
      break;
      label32:
      this.dequeuedInputBuffer = ((SubtitleInputBuffer)this.availableInputBuffers.pollFirst());
    }
  }
  
  public SubtitleOutputBuffer dequeueOutputBuffer()
    throws SubtitleDecoderException
  {
    Object localObject1 = null;
    Object localObject2;
    if (this.availableOutputBuffers.isEmpty()) {
      localObject2 = localObject1;
    }
    for (;;)
    {
      return (SubtitleOutputBuffer)localObject2;
      SubtitleInputBuffer localSubtitleInputBuffer;
      Subtitle localSubtitle;
      do
      {
        do
        {
          releaseInputBuffer(localSubtitleInputBuffer);
          localObject2 = localObject1;
          if (this.queuedInputBuffers.isEmpty()) {
            break;
          }
          localObject2 = localObject1;
          if (((SubtitleInputBuffer)this.queuedInputBuffers.peek()).timeUs > this.playbackPositionUs) {
            break;
          }
          localSubtitleInputBuffer = (SubtitleInputBuffer)this.queuedInputBuffers.poll();
          if (localSubtitleInputBuffer.isEndOfStream())
          {
            localObject2 = (SubtitleOutputBuffer)this.availableOutputBuffers.pollFirst();
            ((SubtitleOutputBuffer)localObject2).addFlag(4);
            releaseInputBuffer(localSubtitleInputBuffer);
            break;
          }
          decode(localSubtitleInputBuffer);
        } while (!isNewSubtitleDataAvailable());
        localSubtitle = createSubtitle();
      } while (localSubtitleInputBuffer.isDecodeOnly());
      localObject2 = (SubtitleOutputBuffer)this.availableOutputBuffers.pollFirst();
      ((SubtitleOutputBuffer)localObject2).setContent(localSubtitleInputBuffer.timeUs, localSubtitle, Long.MAX_VALUE);
      releaseInputBuffer(localSubtitleInputBuffer);
    }
  }
  
  public void flush()
  {
    this.playbackPositionUs = 0L;
    while (!this.queuedInputBuffers.isEmpty()) {
      releaseInputBuffer((SubtitleInputBuffer)this.queuedInputBuffers.poll());
    }
    if (this.dequeuedInputBuffer != null)
    {
      releaseInputBuffer(this.dequeuedInputBuffer);
      this.dequeuedInputBuffer = null;
    }
  }
  
  public abstract String getName();
  
  protected abstract boolean isNewSubtitleDataAvailable();
  
  public void queueInputBuffer(SubtitleInputBuffer paramSubtitleInputBuffer)
    throws SubtitleDecoderException
  {
    boolean bool;
    if (paramSubtitleInputBuffer == this.dequeuedInputBuffer)
    {
      bool = true;
      Assertions.checkArgument(bool);
      if (!paramSubtitleInputBuffer.isDecodeOnly()) {
        break label37;
      }
      releaseInputBuffer(paramSubtitleInputBuffer);
    }
    for (;;)
    {
      this.dequeuedInputBuffer = null;
      return;
      bool = false;
      break;
      label37:
      this.queuedInputBuffers.add(paramSubtitleInputBuffer);
    }
  }
  
  public void release() {}
  
  protected void releaseOutputBuffer(SubtitleOutputBuffer paramSubtitleOutputBuffer)
  {
    paramSubtitleOutputBuffer.clear();
    this.availableOutputBuffers.add(paramSubtitleOutputBuffer);
  }
  
  public void setPositionUs(long paramLong)
  {
    this.playbackPositionUs = paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/cea/CeaDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */