package org.telegram.messenger.exoplayer.chunk;

public final class ChunkOperationHolder
{
  public Chunk chunk;
  public boolean endOfStream;
  public int queueSize;
  
  public void clear()
  {
    this.queueSize = 0;
    this.chunk = null;
    this.endOfStream = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/chunk/ChunkOperationHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */