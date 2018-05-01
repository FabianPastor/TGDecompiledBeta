package org.telegram.messenger.exoplayer.util.extensions;

public abstract class OutputBuffer
  extends Buffer
{
  public long timestampUs;
  
  public abstract void release();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/extensions/OutputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */