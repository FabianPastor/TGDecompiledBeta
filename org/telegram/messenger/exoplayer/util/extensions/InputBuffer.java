package org.telegram.messenger.exoplayer.util.extensions;

import org.telegram.messenger.exoplayer.SampleHolder;

public class InputBuffer
  extends Buffer
{
  public final SampleHolder sampleHolder = new SampleHolder(2);
  
  public void reset()
  {
    super.reset();
    this.sampleHolder.clearData();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/extensions/InputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */