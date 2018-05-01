package org.telegram.messenger.exoplayer2.decoder;

public abstract class OutputBuffer
  extends Buffer
{
  public int skippedOutputBufferCount;
  public long timeUs;
  
  public abstract void release();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/decoder/OutputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */